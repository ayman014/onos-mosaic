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

import java.util.concurrent.atomic.AtomicLong;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator; 
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Set;
import java.util.Iterator;
import java.lang.Integer;

import org.onlab.osgi.DefaultServiceDirectory;
import org.onlab.util.Bandwidth;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import org.onlab.osgi.DefaultServiceDirectory;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.net.DeviceId;
import org.onosproject.net.config.NetworkConfigService;
import org.onosproject.net.config.basics.BasicDeviceConfig;

import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;


@Service
@Command(scope = "onos", name = "qos-add",
        description = "Qos Test.")
public class QoSAddCommand extends AbstractShellCommand {

    @Argument(index = 0, name = "ovsdbDeviceId", description = "the ovsdb TYPE id.", required = true,
            multiValued = false)
    String ovsdbDeviceId = null;

    @Argument(index = 1, name = "qos id", description = "Qos id.", required = true,
            multiValued = false)
    String id = null;

    /*@Argument(index = 2, name = "port name", description = "the port name.", required = true,
            multiValued = false)
    String portName = null;*/

    @Argument(index = 2, name = "port number", description = "the port number.", required = true,
            multiValued = false)
    String portnumber = null;

    @Argument(index = 3, name = "rate", description = "the rate of interface.", required = true,
            multiValued = false)
    String rate = null;

    @Argument(index = 4, name = "burst", description = "the burst of interface.", required = true,
            multiValued = false)
    String burst = null;
   
    @Argument(index = 5, name = "dscp", description = "Description service Code Point", required = false,
            multiValued = false)
    int dscp = 0; // low priority by default
    @Argument(index = 6, name = "priority", description = "Priority", required = false,
            multiValued = false)
    long priority = 0; // lowest priority by default

    private boolean qosNoTExist=true;

       protected long availableQueueId (QueueConfigBehaviour queueConfig){
        AtomicLong queueCounter = new AtomicLong(0);
        TreeMap<String, QueueDescription> treeQueues = new TreeMap<String, QueueDescription>();
	if(queueConfig.getQueues().isEmpty()) return 0;
	else {
        queueConfig.getQueues().stream().forEach(q -> {
            treeQueues.put(String.valueOf(q.queueId()), q);
        });
        for (Map.Entry<String, QueueDescription> entry : treeQueues.entrySet()){
            String index[] = (entry.getKey()).split("\\.");
	  //////////////////
                print("Map of Queueus, currentID to Test = %s , index[%s] = %s",entry.getKey() ,String.valueOf(queueCounter.get()), index[0]);
          ///////////////////
	if(index[1].equals(id)){
            	if(Long.valueOf(index[0])<=queueCounter.longValue())  queueCounter.getAndIncrement();
            	else { print("Condition met Getting Out from the loop ! "); break; }
		}
             }
	}
	print("Available Queue ID = %s", String.valueOf(queueCounter.get()));
        return queueCounter.get();
    }
    protected TreeMap<String, QueueDescription> sortedQueues(QueueConfigBehaviour queueConfig){
        TreeMap<String, QueueDescription> queues = new TreeMap<String, QueueDescription>();
        queueConfig.getQueues().stream().forEach(q -> {
            queues.put(q.queueId().toString(), q);
        });
        return queues;
    }
    protected QosDescription qosDescCreator(String name, Map <Long, QueueDescription> longerQueues){
	QosDescription qosDescription = DefaultQosDescription.builder()
                .qosId(QosId.qosId(name))
                .type(QosDescription.Type.HTB)
                .maxRate(Bandwidth.bps(Long.valueOf("10000")))
                .queues(longerQueues)
                .build();
	return qosDescription;

    }

    @Override
    protected void doExecute() {
	//qosNoTExist=true;
        DeviceService deviceService = DefaultServiceDirectory.getService(DeviceService.class);
        Device device = deviceService.getDevice(DeviceId.deviceId(ovsdbDeviceId));
        if (device == null) {
            log.error("{} isn't support config.", ovsdbDeviceId);
            return;
        }

        QueueConfigBehaviour queueConfig = device.as(QueueConfigBehaviour.class);
        QosConfigBehaviour qosConfig = device.as(QosConfigBehaviour.class);
        PortConfigBehaviour portConfig = device.as(PortConfigBehaviour.class);
	Long queueNumericId = availableQueueId(queueConfig);
	String queueID = queueNumericId + "." + id;

        QueueDescription queueDesc = DefaultQueueDescription.builder()
                .queueId(QueueId.queueId(queueID))
                .maxRate(Bandwidth.bps(Long.parseLong(rate)))
                .minRate(Bandwidth.bps(Long.valueOf(rate)))
                .burst(Long.valueOf(burst))
		.dscp(dscp)
		.priority(priority)
                .build();

	PortNumber pn = PortNumber.portNumber(Long.valueOf(portnumber), id); // id
        PortDescription portDesc = new DefaultPortDescription(pn, true);

 	Map<Long, QueueDescription> longQueues = new HashMap<>();

       qosConfig.getQoses().forEach(q -> {
       TreeMap<String, QueueDescription> queuesMap =  sortedQueues(queueConfig);
        for (Map.Entry<String, QueueDescription> entry : queuesMap.entrySet()){
            if(entry.getKey().matches("(.*)" + id)){
                String QueueQosID[]= entry.getKey().split("\\.");
		//queues.put(entry.getKey() , entry.getValue());
                longQueues.put(Long.valueOf(QueueQosID[0]), entry.getValue());
            }
        }
        });
	//queueNumericId
	longQueues.put(queueNumericId, queueDesc);
	QosDescription qosDesc = qosDescCreator(id, longQueues);

	if(!qosConfig.getQoses().isEmpty()){
		qosConfig.getQoses().stream().forEach(qoses ->{
         		if(String.valueOf(qoses.qosId()).equals(id)) { 
				qosNoTExist=false;
				print("QoS exists, add just the queue to the QoS");
				//qosDesc = qosDescCreator(qoses.qosId(), qoses.queues()); 
			}
	        });
	}

	if(qosDesc.queues().isPresent()) {
                print("qosDesc.queues() : queues Exists");

                for (Map.Entry<Long, QueueDescription> entry : (qosDesc.queues().get()).entrySet()) {
                    print("Key : %d - Inside QueueId : %s ", entry.getKey(), entry.getValue().queueId());
                }
            }
        else print("qosDesc.queues() ==> qoses.queues() = No queues");

	print("Addqueue");
	queueConfig.addQueue(queueDesc);
	print("queue added");
	if(!qosNoTExist){
		print("QoS exists !\nQoS ID (port)=%s, maxRate=%s, cbs=%s, cir=%s, " +
                                "queues=%s, type=%s", qosDesc.qosId(), qosDesc.maxRate(),
                qosDesc.cbs(), qosDesc.cir(), qosDesc.queues(), qosDesc.type());
		qosConfig.deleteQoS(qosDesc.qosId());
		portConfig.removeQoS(pn);
		
	}
	        qosConfig.addQoS(qosDesc);
                qosConfig.insertQueues(QosId.qosId(id), longQueues);
                //qosConfig.addQoS(qosDesc);
                portConfig.applyQoS(portDesc, qosDesc);

        //qosConfig.addQoS(qosDesc);
 	//qosConfig.insertQueues(QosId.qosId(id), longQueues);
	print("Queues inserted");
	//if(!qosExistance){
	//portConfig.applyQoS(portDesc, qosDesc);
        //        print("Condition I : Qos does not exists apply qos to the port");
	//}
        qosConfig.getQoses().stream().forEach(qoses ->{
            if(qoses.queues().isPresent()) {
                print("queues present");

                for (Map.Entry<Long, QueueDescription> entry : (qoses.queues().get()).entrySet()) {
                    print("\nPhase 3 Queues Map :");
                    print("Key : %d - Inside QueueId : %s ", entry.getKey(), entry.getValue().queueId());
                }
            }
            else print("qosConfig ==> qoses.queues() = No queues");
        });
    }
}

