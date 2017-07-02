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
package scouter.client.counter.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import scouter.client.Images;
import scouter.client.server.ServerManager;
import scouter.client.util.ImageUtil;
import scouter.client.views.DigitalCountView;
import scouter.client.visitor.views.VisitorRealtimeView;
import scouter.lang.pack.MapPack;
import scouter.net.RequestCmd;

public class OpenUniqueTotalVisitorAction extends Action {
	public final static String ID = OpenUniqueTotalVisitorAction.class.getName();

	private final IWorkbenchWindow win;
	private String objType;
	private int serverId;

	public OpenUniqueTotalVisitorAction(IWorkbenchWindow win, int serverId, String objType) {
		this.win = win;
		this.objType = objType;
		this.serverId = serverId;
		setText("Today Visitor");
		setImageDescriptor(ImageUtil.getImageDescriptor(Images.monitor));
	}

	public void run() {
		if (win != null) {
			try {
				VisitorRealtimeView view = (VisitorRealtimeView) win.getActivePage().showView(
						VisitorRealtimeView.ID, serverId + "&" + objType, IWorkbenchPage.VIEW_ACTIVATE);
				if (view != null) {
					MapPack param = new MapPack();
					param.put("objType", this.objType);
					String displayObjType = ServerManager.getInstance().getServer(serverId).getCounterEngine().getDisplayNameObjectType(objType);
					view.setInput(displayObjType + "'s  Visitors", RequestCmd.VISITOR_REALTIME_TOTAL, param);
				}
			} catch (PartInitException e) {
				MessageDialog.openError(win.getShell(), "Error", "Error opening view:" + e.getMessage());
			}
		}
	}
}
