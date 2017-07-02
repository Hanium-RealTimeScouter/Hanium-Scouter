/*
 *  Copyright 2015 the original author or authors. 
 *  @https://github.com/scouter-project/scouter
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 */
package scouter.server;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scouter.lang.Counter;
import scouter.lang.Family;
import scouter.lang.ObjectType;
import scouter.lang.counters.CounterEngine;
import scouter.lang.pack.MapPack;
import scouter.server.util.XmlUtil;
import scouter.util.FileUtil;


public class CounterManager {

	private static final String CUSTOM_FILENAME = "counters.site.xml";
	
	private static volatile CounterManager instance;
	CounterEngine engine = new CounterEngine();
	File customFile;
	
	byte[] xmlContent;
	byte[] xmlCustomContent;
	
	
	public static CounterManager getInstance() {
		if (instance == null) {
			synchronized (CounterManager.class) {
				if (instance == null) {
					instance = new CounterManager();
				}
			}
		}
		return instance;
	}
	
	private CounterManager() {
		init();
	}
	
	private void init() {
		readAndParseXml("/scouter/lang/counters/counters.xml");
		customFile = new File(Configure.CONF_DIR + CUSTOM_FILENAME);
		if (customFile.canRead()) {
			xmlCustomContent = FileUtil.readAll(customFile);
			engine.parse(xmlCustomContent);
		}
	}
	
	private void readAndParseXml(String path) {
		InputStream in  = CounterEngine.class.getResourceAsStream(path);
		try {
			xmlContent = FileUtil.readAll(in);
			engine.parse(xmlContent);
		} catch (Exception e) {
			Logger.println("Failed read " + path);
		} finally {
			FileUtil.close(in);
		}
	}
	
	public CounterEngine getCounterEngine() {
		return engine;
	}
	
	public byte[] getXmlContent() {
		return xmlContent;
	}
	
	public byte[] getXmlCustomContent() {
		return xmlCustomContent;
	}
	
	public synchronized boolean addFamily(Family family) {
		Document doc = appendFamily(family, getCustomDocument());
		if (doc != null) {
			XmlUtil.writeXmlFileWithIndent(doc, customFile, 2);
			xmlCustomContent = FileUtil.readAll(customFile);
			reloadEngine();
			return true;
		}
		return false;
	}
	
	public synchronized boolean addFamilyAndObjectType(Family family, ObjectType objectType) {
		Document doc = appendFamily(family, getCustomDocument());
		if (doc != null) {
			doc = appendObjectType(objectType, doc);
			if (doc != null) {
				XmlUtil.writeXmlFileWithIndent(doc, customFile, 2);
				xmlCustomContent = FileUtil.readAll(customFile);
				reloadEngine();
				return true;
			}
		}
		return false;
	}
	
	public boolean addObjectType(MapPack param) {
		String name = param.getText(CounterEngine.ATTR_NAME);
		if (engine.getObjectType(name) != null) {
			return false;
		}
		String displayName = param.getText(CounterEngine.ATTR_DISPLAY);
		String family = param.getText(CounterEngine.ATTR_FAMILY);
		String icon = param.getText(CounterEngine.ATTR_ICON);
		boolean subobject = param.getBoolean(CounterEngine.ATTR_SUBOBJECT);
		ObjectType objType = new ObjectType();
		objType.setName(name);
		objType.setDisplayName(displayName);
		objType.setIcon(icon);
		objType.setFamily(engine.getFamily(family));
		objType.setSubObject(subobject);
		return addObjectType(objType);
	}
	
	public synchronized boolean addObjectType(ObjectType objType) {
		Document doc = appendObjectType(objType, getCustomDocument());
		if (doc != null) {
			XmlUtil.writeXmlFileWithIndent(doc, customFile, 2);
			xmlCustomContent = FileUtil.readAll(customFile);
			reloadEngine();
			return true;
		}
		return false;
	}
	
	public boolean editObjectType(MapPack param) {
		String name = param.getText(CounterEngine.ATTR_NAME);
		String displayName = param.getText(CounterEngine.ATTR_DISPLAY);
		String family = param.getText(CounterEngine.ATTR_FAMILY);
		String icon = param.getText(CounterEngine.ATTR_ICON);
		boolean subobject = param.getBoolean(CounterEngine.ATTR_SUBOBJECT);
		ObjectType objType = new ObjectType();
		objType.setName(name);
		objType.setDisplayName(displayName);
		objType.setIcon(icon);
		objType.setFamily(engine.getFamily(family));
		objType.setSubObject(subobject);
		return editObjectType(objType);
	}
	
	public synchronized boolean editObjectType(ObjectType objType) {
		Document doc = editOrAppendObjectType(objType, getCustomDocument());
		if (doc != null) {
			XmlUtil.writeXmlFileWithIndent(doc, customFile, 2);
			xmlCustomContent = FileUtil.readAll(customFile);
			reloadEngine();
			return true;
		}
		return false;
	}
	
	private Document appendObjectType(ObjectType objType, Document doc) {
		try {
			Element rootElement = (Element) doc.getElementsByTagName(CounterEngine.TAG_COUNTERS).item(0);
			Element typesElements = (Element) doc.getElementsByTagName(CounterEngine.TAG_TYPES).item(0);
			if (rootElement == null) {
				rootElement = doc.createElement(CounterEngine.TAG_COUNTERS);
				doc.appendChild(rootElement);
			}
			if (typesElements == null) {
				typesElements = doc.createElement(CounterEngine.TAG_TYPES);
				rootElement.appendChild(typesElements);
			}
			Element objElement = doc.createElement(CounterEngine.TAG_OBJECT_TYPE);
			setObjectTypeAttribute(doc, objElement, objType);
			typesElements.appendChild(objElement);
		} catch (Exception e) {
			Logger.printStackTrace(e);
			return null;
		}
		return doc;
	}
	
	private Document editOrAppendObjectType(ObjectType objType, Document doc) {
		try {
			Element rootElement = (Element) doc.getElementsByTagName(CounterEngine.TAG_COUNTERS).item(0);
			Element typesElements = (Element) doc.getElementsByTagName(CounterEngine.TAG_TYPES).item(0);
			boolean found = false;
			if (rootElement == null) {
				rootElement = doc.createElement(CounterEngine.TAG_COUNTERS);
				doc.appendChild(rootElement);
			}
			if (typesElements == null) {
				typesElements = doc.createElement(CounterEngine.TAG_TYPES);
				rootElement.appendChild(typesElements);
			}
			NodeList list = doc.getElementsByTagName(CounterEngine.TAG_OBJECT_TYPE);
			if (list == null || list.getLength() < 1) {
				Element objElement = doc.createElement(CounterEngine.TAG_OBJECT_TYPE);
				setObjectTypeAttribute(doc, objElement, objType);
				typesElements.appendChild(objElement);
				found = true;
			} else {
				for (int i = 0; i <  list.getLength(); i++) {
					Node node = list.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element objElement = (Element) node;
						String name = objElement.getAttribute(CounterEngine.ATTR_NAME);
						if (objType.getName().equals(name)) {
							setObjectTypeAttribute(doc, objElement, objType);
							found = true;
							break;
						}
					}
				}
				if (found == false) {
					Element objElement = doc.createElement(CounterEngine.TAG_OBJECT_TYPE);
					setObjectTypeAttribute(doc, objElement, objType);
					typesElements.appendChild(objElement);
					found = true;
				}
			}
			if (found) {
				return doc;
			}
		} catch (Exception e) {
			Logger.printStackTrace(e);
			return null;
		}
		return null;
	}
	
	private static void setObjectTypeAttribute(Document doc, Element objElement, ObjectType objType) {
		objElement.setAttribute(CounterEngine.ATTR_NAME, objType.getName());
		objElement.setAttribute(CounterEngine.ATTR_DISPLAY, objType.getDisplayName());
		objElement.setAttribute(CounterEngine.ATTR_FAMILY, objType.getFamily().getName());
		objElement.setAttribute(CounterEngine.ATTR_ICON, objType.getIcon());
		objElement.setAttribute(CounterEngine.ATTR_SUBOBJECT, objType.isSubObject() ? "true" : "false");
		for (Counter counter : objType.listObjectTypeCounters()) {
			Element counterElement = doc.createElement(CounterEngine.TAG_COUNTER);
			counterElement.setAttribute(CounterEngine.ATTR_NAME, counter.getName());
			counterElement.setAttribute(CounterEngine.ATTR_DISPLAY, counter.getDisplayName());
			counterElement.setAttribute(CounterEngine.ATTR_UNIT, counter.getUnit());
			counterElement.setAttribute(CounterEngine.ATTR_ICON, counter.getIcon());
			counterElement.setAttribute(CounterEngine.ATTR_ALL, counter.isAll() ? "true" : "false");
			counterElement.setAttribute(CounterEngine.ATTR_TOTAL, counter.isTotal() ? "true" : "false");
			objElement.appendChild(counterElement);
		}
	}
	
	private Document appendFamily(Family family, Document doc) {
		try {
			Element rootElement = (Element) doc.getElementsByTagName(CounterEngine.TAG_COUNTERS).item(0);
			Element familysElement = (Element) doc.getElementsByTagName(CounterEngine.TAG_FAMILYS).item(0);
			if (rootElement == null) {
				rootElement = doc.createElement(CounterEngine.TAG_COUNTERS);
				doc.appendChild(rootElement);
			}
			if (familysElement == null) {
				familysElement = doc.createElement(CounterEngine.TAG_FAMILYS);
				rootElement.appendChild(familysElement);
			}
			Element familyElement = doc.createElement(CounterEngine.TAG_FAMILY);
			familyElement.setAttribute(CounterEngine.ATTR_NAME, family.getName());
			familyElement.setAttribute(CounterEngine.ATTR_MASTER, family.getMaster());
			familysElement.appendChild(familyElement);
			for (Counter counter : family.listCounters()) {
				Element counterElement = doc.createElement(CounterEngine.TAG_COUNTER);
				counterElement.setAttribute(CounterEngine.ATTR_NAME, counter.getName());
				counterElement.setAttribute(CounterEngine.ATTR_DISPLAY, counter.getDisplayName());
				counterElement.setAttribute(CounterEngine.ATTR_UNIT, counter.getUnit());
				counterElement.setAttribute(CounterEngine.ATTR_ICON, counter.getIcon());
				counterElement.setAttribute(CounterEngine.ATTR_ALL, counter.isAll() ? "true" : "false");
				counterElement.setAttribute(CounterEngine.ATTR_TOTAL, counter.isTotal() ? "true" : "false");
				familyElement.appendChild(counterElement);
			}
		} catch (Exception e) {
			Logger.printStackTrace(e);
			return null;
		}
		return doc;
	}
	
	private Document getCustomDocument() {
		Document doc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();
			if (customFile.canRead()) {
				doc = builder.parse(customFile);
				doc.getDocumentElement().normalize();
			} else {
				doc = builder.newDocument();
			}
		} catch (Exception e) {
			Logger.printStackTrace(e);
		}
		return doc;
	}
	
	public static void main(String[] args) {
		File f = Configure.getInstance().getPropertyFile();
		System.out.println(f.getParent());
	}
	
	private void reloadEngine() {
		engine.clear();
		engine.parse(xmlContent);
		engine.parse(xmlCustomContent);
	}
}
