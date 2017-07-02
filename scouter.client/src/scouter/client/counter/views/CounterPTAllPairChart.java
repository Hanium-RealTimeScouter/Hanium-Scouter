package scouter.client.counter.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.csstudio.swt.xygraph.dataprovider.CircularBufferDataProvider;
import org.csstudio.swt.xygraph.dataprovider.Sample;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import scouter.client.counter.actions.OpenPTPairAllAction;
import scouter.client.net.INetReader;
import scouter.client.net.TcpProxy;
import scouter.client.server.Server;
import scouter.client.server.ServerManager;
import scouter.client.util.ChartUtil;
import scouter.client.util.ConsoleProxy;
import scouter.client.util.CounterUtil;
import scouter.client.util.ExUtil;
import scouter.io.DataInputX;
import scouter.lang.counters.CounterEngine;
import scouter.lang.pack.MapPack;
import scouter.lang.value.ListValue;
import scouter.lang.value.Value;
import scouter.lang.value.ValueEnum;
import scouter.net.RequestCmd;
import scouter.util.CastUtil;
import scouter.util.DateUtil;
import scouter.util.StringUtil;

public class CounterPTAllPairChart extends CounterAllPairPainter {
	
	public final static String ID = CounterPTAllPairChart.class.getName();

	private int serverId;
	private String objType;
	private String counter;
	private long stime;
	private long etime;
	
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
		IToolBarManager man = getViewSite().getActionBars().getToolBarManager();
		man.add(new OpenPTPairAllAction(getViewSite().getWorkbenchWindow(), "Load", serverId, objType, counter));
	}
	
	public void setInput(long stime, long etime) {
		this.stime = stime;
		this.etime = etime;
		try {
			setViewTab(objType, counter, serverId);
			Server server = ServerManager.getInstance().getServer(serverId);
			CounterEngine ce = server.getCounterEngine();
			String counterName = ce.getCounterDisplayName(objType, counter);
			desc = "ⓢ" + server.getName() + " | (Past All) " + counterName + "(" + ce.getCounterUnit(objType, counter) + ") "
					+ DateUtil.format(stime, "yyyyMMdd HH:mm:ss") + " ~ " + DateUtil.format(etime, "HH:mm:ss");
			this.xyGraph.primaryXAxis.setRange(stime, etime);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Set<Integer> keySet = dataMap.keySet();
		for (Integer key : keySet) {
			TracePair tp = dataMap.get(key);
			xyGraph.removeTrace(tp.totalTrace);
			xyGraph.removeTrace(tp.activeTrace);
		}
		dataMap.clear();
		load();
	}
	
	private void load() {
		CounterEngine counterEngine = ServerManager.getInstance().getServer(serverId).getCounterEngine();
		new Job("Load " + counterEngine.getCounterDisplayName(objType, counter)) {
			protected IStatus run(IProgressMonitor monitor) {
				final List<MapPack> values = new ArrayList<MapPack>();
				TcpProxy tcp = TcpProxy.getTcpProxy(serverId);
				try {
					MapPack param = new MapPack();
					param.put("stime", stime);
					param.put("etime", etime);
					param.put("objType", objType);
					param.put("counter", counter);
					
					tcp.process(RequestCmd.COUNTER_PAST_TIME_ALL, param, new INetReader() {
						public void process(DataInputX in) throws IOException {
							MapPack mpack = (MapPack) in.readPack();
							values.add(mpack);
						};
					});
				} catch (Throwable t) {
					ConsoleProxy.errorSafe(t.toString());
				} finally {
					TcpProxy.putTcpProxy(tcp);
				}
				
				ExUtil.exec(canvas, new Runnable() {
					public void run() {
						double max = 0;
						for (MapPack mpack : values) {
							int objHash = mpack.getInt("objHash");
							ListValue time = mpack.getList("time");
							ListValue value = mpack.getList("value");
							TracePair tp = getTracePair(objType, objHash, (int) ((etime - stime) / (DateUtil.MILLIS_PER_SECOND * 2)));
							CircularBufferDataProvider maxProvider = (CircularBufferDataProvider) tp.totalTrace.getDataProvider();
							CircularBufferDataProvider valueProvider = (CircularBufferDataProvider) tp.activeTrace.getDataProvider();
							maxProvider.clearTrace();
							valueProvider.clearTrace();
							for (int i = 0; time != null && i < time.size(); i++) {
								long x = time.getLong(i);
								Value v = value.get(i);
								if (v != null && v.getValueType() == ValueEnum.LIST) {
									ListValue lv = (ListValue) v;
									maxProvider.addSample(new Sample(x, lv.getDouble(0)));
									valueProvider.addSample(new Sample(x, lv.getDouble(1)));
								}
							}
							max = Math.max(ChartUtil.getMax(maxProvider.iterator()), max);
						}
						if (CounterUtil.isPercentValue(objType, counter)) {
							xyGraph.primaryYAxis.setRange(0, 100);
						} else {
							xyGraph.primaryYAxis.setRange(0, max);
						}
						redraw();
					}
				});
				
				return Status.OK_STATUS;
			}
			
		}.schedule();
	}
}
