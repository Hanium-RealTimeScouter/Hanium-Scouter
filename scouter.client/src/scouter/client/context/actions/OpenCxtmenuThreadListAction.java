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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import scouter.client.views.ObjectThreadListView;

public class OpenCxtmenuThreadListAction extends Action {
	public final static String ID = OpenCxtmenuThreadListAction.class.getName();

	private final IWorkbenchWindow win;
	private int objHash;
	private int serverId;

	public OpenCxtmenuThreadListAction(IWorkbenchWindow win, String label, /*Image image, */int objHash, int serverId) {
		this.win = win;
		this.objHash = objHash;
		this.serverId = serverId;
		setText(label);
//		setImageDescriptor(ImageUtil.getImageDescriptor(image));

	}

	public void run() {
		if (win != null) {
			try {
				ObjectThreadListView view = (ObjectThreadListView) win.getActivePage().showView(
						ObjectThreadListView.ID, "" + objHash, IWorkbenchPage.VIEW_ACTIVATE);
				if (view != null) {
					view.setInput(objHash, serverId);
				}
			} catch (PartInitException e) {
				MessageDialog.openError(win.getShell(), "Error", "Error opening view:" + e.getMessage());
			}
		}
	}
}
