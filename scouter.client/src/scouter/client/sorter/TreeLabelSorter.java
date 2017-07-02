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
package scouter.client.sorter;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import scouter.util.CastUtil;

public class TreeLabelSorter extends ViewerComparator {
	public static final int ORDER_ASC = 1;
	public static final int NONE = 0;
	public static final int ORDER_DESC = -1;

	private TreeColumn col = null;
	private int colIndex = 0;
	ICustomCompare custom;
	TreeViewer viewer;
	Tree tree;
	private int dir = 0;
	
	
	public TreeLabelSorter(TreeViewer viewer) {
		this.viewer = viewer;
		this.tree = viewer.getTree();
	}
	
	public TreeLabelSorter setCustomCompare(ICustomCompare custom) {
		this.custom = custom;
		return this;
	}
	
	@Override
	public int compare(Viewer viewer, Object o1, Object o2) {
		if (dir == NONE || this.col == null) {
			return 0;
		}
		if (custom == null) {
			return dir * compareNormal(o1, o2);
		} else {
			return dir * custom.doCompare(this.col, this.colIndex, o1, o2);
		}
	}
	
	public void setColumn(TreeColumn clickedColumn) {
		if (col == clickedColumn) {
			dir = dir * -1;
		} else {
			this.col = clickedColumn;
			this.dir = ORDER_ASC;
		}
		TreeColumn[] cols = tree.getColumns();
		int colLen = cols.length;;
		for (int i = 0; i < colLen; i++) {
			if (cols[i] == this.col) {
				colIndex = i;
				break;
			}
		}
		tree.setSortColumn(clickedColumn);
		switch (dir) {
		case ORDER_ASC:
			tree.setSortDirection(SWT.UP);
			break;
		case ORDER_DESC:
			tree.setSortDirection(SWT.DOWN);
			break;
		}
		viewer.refresh();
	}

	protected int compareNormal(Object e1, Object e2) {
		ITableLabelProvider labelProvider = (ITableLabelProvider) viewer.getLabelProvider();
		String t1 = labelProvider.getColumnText(e1, colIndex);
		String t2 = labelProvider.getColumnText(e2, colIndex);
		Boolean isNumber = (Boolean) this.col.getData("isNumber");
		if (isNumber != null && isNumber.booleanValue()) {
			t1 = ColumnLabelSorter.numonly(t1);
			t2 = ColumnLabelSorter.numonly(t2);
			double n1 = CastUtil.cdouble(t1);
			double n2 = CastUtil.cdouble(t2);
			return n1 == n2 ? 0 : (n1 > n2) ? 1 : -1;
		} else {
			if (t1 == null) t1 = "";
			if (t2 == null) t2 = "";
		}
		return t1.compareTo(t2);
	}
	
	public static interface ICustomCompare {
		public int doCompare(TreeColumn col, int index, Object o1, Object o2);
	}
}