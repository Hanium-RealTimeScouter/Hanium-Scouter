package scouter.client.counter.views;

import java.util.HashMap;
import java.util.Iterator;

import org.csstudio.swt.xygraph.dataprovider.CircularBufferDataProvider;
import org.csstudio.swt.xygraph.dataprovider.Sample;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import scouter.client.counter.actions.OpenPTPairAllAction;
import scouter.client.model.RefreshThread;
import scouter.client.model.RefreshThread.Refreshable;
import scouter.client.net.TcpProxy;
import scouter.client.server.Server;
import scouter.client.server.ServerManager;
import scouter.client.threads.ObjectSelectManager;
import scouter.client.util.ConsoleProxy;
import scouter.client.util.CounterUtil;
import scouter.client.util.ExUtil;
import scouter.client.util.TimeUtil;
import scouter.lang.counters.CounterEngine;
import scouter.lang.pack.MapPack;
import scouter.lang.value.ListValue;
import scouter.lang.value.Value;
import scouter.lang.value.ValueEnum;
import scouter.net.RequestCmd;
import scouter.util.CastUtil;
import scouter.util.DateUtil;
import scouter.util.StringUtil;

public class CounterRTAllPairChart extends CounterAllPairPainter implements Refreshable {
	
	public final static String ID = CounterRTAllPairChart.class.getName();

	protected RefreshThread thread = null;
	
	private int serverId;
	private String objType;
	private String counter;
	
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		String secId = site.getSecondaryId();
		String[] ids = StringUtil.split(secId, "&");
		this.serverId = CastUtil.cint(ids[0]);
		this.objType = ids[1];
		this.counter = ids[2];
	}
	
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		try {
			setViewTab(objType, counter, serverId);
			Server server = ServerManager.getInstance().getServer(serverId);
			CounterEngine ce = server.getCounterEngine();
			String counterName = ce.getCounterDisplayName(objType, counter);
			desc = "ⓢ" + server.getName() + " | (Current All) " + counterName + "(" + ce.getCounterUnit(objType, counter) + ")";
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		IToolBarManager man = getViewSite().getActionBars().getToolBarManager();
		man.add(new OpenPTPairAllAction(getViewSite().getWorkbenchWindow(), "Load", serverId, objType, counter));
		
		thread = new RefreshThread(this, 2000);
		thread.setName(this.toString() + " - " + "objType:" + objType + ", counter:" + counter + ", serverId:" + serverId);
		thread.start();
	}
	
	public void refresh() {
		final HashMap<Integer, Value> values = new HashMap<Integer, Value>();
		TcpProxy tcp = TcpProxy.getTcpProxy(serverId);
		try {
			MapPack param = new MapPack();

			param.put("objType", objType);
			param.put("counter", counter);

			MapPack out = (MapPack) tcp.getSingle(RequestCmd.COUNTER_REAL_TIME_ALL, param);
			isActive = false;
			if (out != null) {
				ListValue objHash = out.getList("objHash");
				ListValue v = out.getList("value");
				for (int i = 0; i < objHash.size(); i++) {
					values.put(CastUtil.cint(objHash.get(i)), v.get(i));
					isActive = true;
				}
			}
		} catch (Throwable t) {
			ConsoleProxy.errorSafe(t.toString());
		} finally {
			TcpProxy.putTcpProxy(tcp);
		}
		
		ExUtil.exec(canvas, new Runnable() {
			public void run() {
				if (isActive) {
					setActive();
				} else {
					setInactive();
				}

				long now = TimeUtil.getCurrentTime(serverId);
				xyGraph.primaryXAxis.setRange(now - DateUtil.MILLIS_PER_MINUTE * 5, now + 1);
				Iterator<Integer> itr = values.keySet().iterator();
				while (itr.hasNext()) {
					int objHash = itr.next();
					Value value = values.get(objHash);
					if (value != null && value.getValueType() == ValueEnum.LIST) {
						ListValue lv = (ListValue) value;
						TracePair tp = getTracePair(objType, objHash, 155);
						CircularBufferDataProvider provider1 = (CircularBufferDataProvider) tp.totalTrace.getDataProvider();
						CircularBufferDataProvider provider2 = (CircularBufferDataProvider) tp.activeTrace.getDataProvider();
						provider1.addSample(new Sample(now, CastUtil.cdouble(lv.get(0))));
						provider2.addSample(new Sample(now, CastUtil.cdouble(lv.get(1))));
					}

				}
				if (CounterUtil.isPercentValue(objType, counter)) {
					xyGraph.primaryYAxis.setRange(0, 100);
				} else {
					double max = getMaxValue();
					xyGraph.primaryYAxis.setRange(0, max);
				}
				redraw();
			}
		});
	}
	
	@Override
	public void dispose() {
		super.dispose();
		if (this.thread != null) {
			this.thread.shutdown();
		}
	}
}
