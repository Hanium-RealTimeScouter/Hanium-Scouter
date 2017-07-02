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
 *
 */
package scouter.client.context.actions;

import org.eclipse.jface.action.Action;

import scouter.client.net.TcpProxy;
import scouter.client.util.ConsoleProxy;
import scouter.client.util.ExUtil;
import scouter.util.StringUtil;
import scouter.lang.pack.MapPack;
import scouter.lang.pack.Pack;
import scouter.net.RequestCmd;

public class OpenCxtmenuSystemGcAction extends Action {
	public final static String ID = OpenCxtmenuSystemGcAction.class.getName();

	private int objHash;
	private int serverId;
	public OpenCxtmenuSystemGcAction(String label, int objHash, int serverId) {
		this.objHash = objHash;
		this.serverId = serverId;
		setText(label);
	}

	public void run() {
		ExUtil.asyncRun(new Runnable() {
			public void run() {
				TcpProxy tcp = TcpProxy.getTcpProxy(serverId);
				try {
					MapPack param = new MapPack();
					param.put("objHash", objHash);
					tcp.getSingle(RequestCmd.OBJECT_SYSTEM_GC, param);
					ConsoleProxy.infoSafe("triggerd system.gc");
				} finally {
					TcpProxy.putTcpProxy(tcp);
				}
			}
		});
	}
}
