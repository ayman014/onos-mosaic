package org.onosproject.cli.net;
import org.onosproject.net.behaviour.QueueConfigBehaviour; //interface which contains the addQueue(QueueDescription method)
import org.onosproject.net.behaviour.QueueDescription.Type;// for the enumeration
import org.onosproject.net.behaviour.QueueDescription;// the interface QueueDescription which should be invoked first
import org.onosproject.net.behaviour.DefaultQueueDescription;// to instance a DefautlQueueDescription which is an implementation of QueueDescription class
import org.onosproject.net.behaviour.QueueId;// to invok the QueueId Constructor
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.behaviour.DefaultQosDescription;
import org.onosproject.net.behaviour.QosConfigBehaviour;
import org.onosproject.net.behaviour.PortConfigBehaviour;
import org.onosproject.net.PortNumber;
import org.onosproject.net.device.PortDescription;
import org.onosproject.net.device.DefaultPortDescription;
import org.onosproject.net.behaviour.QosDescription;
import org.onosproject.net.Device;
import org.onosproject.net.behaviour.QosId;
import org.onosproject.net.DeviceId;
import org.onosproject.net.driver.DriverHandler;
import org.onosproject.net.driver.DriverService;


import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.cluster.ClusterService;
import org.onosproject.store.service.LeaderElector;


import java.util.EnumSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.lang.Integer;

import org.onlab.osgi.DefaultServiceDirectory;
import org.onlab.util.Bandwidth;


import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;


@Service
@Command(scope = "onos", name = "qostest",
        description = "Lists information about work queues in the system")
public class QueuesControllersCommand extends AbstractShellCommand {


    @Argument(index = 0, name = "type", description = "the type of qos [add/del/display].", required = true,
            multiValued = false)
    String type = null;

    @Argument(index = 1, name = "controllerid", description = "the CONTROLLER TYPE device id.", required = true,
            multiValued = false)
    String controllerid = null;

    @Argument(index = 2, name = "port name", description = "the port name.", required = true,
            multiValued = false)
    String name = null;

    @Argument(index = 3, name = "port number", description = "the port number.", required = true,
            multiValued = false)
    String portnumber = null;

    @Argument(index = 4, name = "Queue ID", description = "the Queue ID.", required = false,
            multiValued = false)
    String queueNumber = "1";

    @Argument(index = 5, name = "rate", description = "the rate of interface.", required = false,
            multiValued = false)
    String rate = "100";

    /*@Argument(index = 6, name = "burst", description = "the burst of interface.", required = false,
            multiValued = false)
    String burst = "100";*/

    /*@Argument(index = 6, name = "priority", description = "the queue's priority", required = false,
            multiValued = false)
    String priority = "1";*/

    public QueueDescription queueDescriptionCeation(){
        QueueDescription queueDesc = DefaultQueueDescription.builder()
                .queueId(QueueId.queueId(name))
                .dscp(Integer.valueOf(queueNumber))
                .maxRate(Bandwidth.bps(Long.parseLong(rate)))
                .minRate(Bandwidth.bps(Long.valueOf(rate)))
                //.burst(Long.valueOf(burst))
                //.priority(Long.valueOf(priority))
                .build();
        return queueDesc;
    }

    public QosDescription qosDescriptionCeation(Map<Long, QueueDescription> queues){
        QosDescription qosDesc = DefaultQosDescription.builder()
                .qosId(QosId.qosId(name))
                .type(QosDescription.Type.HTB)
                .maxRate(Bandwidth.bps(Long.valueOf("100000")))
                .queues(queues)
                .build();
        return qosDesc;
    }

    @Override
    protected void doExecute() {

/****************************************************************************************/
        DeviceService deviceService = DefaultServiceDirectory.getService(DeviceService.class);
        Device device = deviceService.getDevice(DeviceId.deviceId(controllerid));
        if (device == null) {
            log.error("{} isn't support config.", controllerid);
            return;
        }
 	QueueDescription queueDesc=null;

        if(!type.equals("display")) queueDesc = queueDescriptionCeation();

        PortNumber pn = PortNumber.portNumber(Long.valueOf(portnumber), name);
        PortDescription portDesc = new DefaultPortDescription(pn, true);

 	
	Map<Long, QueueDescription> queues = new HashMap<>();
        QosDescription qosDesc = null;

	if(!type.equals("display")) { 
        queues.put(0L, queueDesc);
        qosDesc = qosDescriptionCeation(queues);
	}

        QueueConfigBehaviour queueConfig = device.as(QueueConfigBehaviour.class);
        QosConfigBehaviour qosConfig = device.as(QosConfigBehaviour.class);
        PortConfigBehaviour portConfig = device.as(PortConfigBehaviour.class);
        if (type.equals("add")) {
	   queueConfig.addQueue(queueDesc);
	   qosConfig.insertQueues(qosDesc.qosId(), queues);
           qosConfig.addQoS(qosDesc);
           portConfig.applyQoS(portDesc, qosDesc);

        } else if (type.equals("del")) {
            queueConfig.deleteQueue(queueDesc.queueId());
            qosConfig.deleteQoS(qosDesc.qosId());
            portConfig.removeQoS(portDesc.portNumber());
        }
        else if(type.equals("delqueues")) {
                queueConfig.getQueues().stream().forEach(q -> {
                    queueConfig.deleteQueue(q.queueId());
                });
        }
        else if(type.equals("delqos")) {
            qosConfig.getQoses().forEach(q -> {
                qosConfig.deleteQoS(q.qosId());
            });

        }
        else if (type.equals("display")) {
            queueConfig.getQueues().stream().forEach(q -> {
                print("Queue ID=%s, maxRate=%s, " +
                                "minRate=%s, priority=%s, burst=%s", q.queueId(),
                        q.maxRate(), q.minRate(),
                        q.priority(), q.burst());
            });
            qosConfig.getQoses().forEach(q -> {
                print("QoS ID (port)=%s, maxRate=%s, cbs=%s, cir=%s, " +
                                "queues=%s, type=%s", q.qosId(), q.maxRate(),
                        q.cbs(), q.cir(), q.queues(), q.type());
            });
        }

    }
}

