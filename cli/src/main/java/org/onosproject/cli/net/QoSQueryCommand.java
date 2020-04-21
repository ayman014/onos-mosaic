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
import java.util.Set; 
import java.util.TreeMap; 

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
@Command(scope = "onos", name = "qos-query",
        description = "Qos Test.")
public class QoSQueryCommand extends AbstractShellCommand {

    @Argument(index = 0, name = "controllerid", description = "the CONTROLLER TYPE device id.", required = true,
            multiValued = false)
    String controllerid = null;

   /* protected List<String> sortedQueues(QueueConfigBehaviour queueConfig){
        List<String> QueueIds = new ArrayList<String>();
        queueConfig.getQueues().stream().forEach(q -> {
            //String index[] = q.queueId().toString().split(".");
            QueueIds.add(q.queueId().toString());
        });
        QueueIds.sort((Comparator.naturalOrder()));
        return QueueIds;
    }*/

    protected TreeMap<String, QueueDescription> sortedQueues(QueueConfigBehaviour queueConfig){
        TreeMap<String, QueueDescription> queues = new TreeMap<String, QueueDescription>();
        queueConfig.getQueues().stream().forEach(q -> {
            queues.put(q.queueId().toString(), q);
        });
        return queues;
    }

    @Override
    protected void doExecute() {
        DeviceService deviceService = DefaultServiceDirectory.getService(DeviceService.class);
        Device device = deviceService.getDevice(DeviceId.deviceId(controllerid));
        if (device == null) {
            log.error("{} isn't support config.", controllerid);
            return;
        }

        QueueConfigBehaviour queueConfig = device.as(QueueConfigBehaviour.class);
        QosConfigBehaviour qosConfig = device.as(QosConfigBehaviour.class);
        PortConfigBehaviour portConfig = device.as(PortConfigBehaviour.class);

      
        qosConfig.getQoses().forEach(q -> {
            print("\nQosID=%s, maxRate=%s, cbs=%s, cir=%s, " +
                            "queues=%s, type=%s", q.qosId(), q.maxRate(),
                    q.cbs(), q.cir(), q.queues(), q.type());
	print("-------------------------------------------------------------------------------------------------");
       TreeMap<String, QueueDescription> queues =  sortedQueues(queueConfig);
        for (Map.Entry<String, QueueDescription> entry : queues.entrySet()){
            if(entry.getKey().matches("(.*)" + String.valueOf(q.qosId()))){
                print("QueueID=%s, type=%s, dscp=%s, maxRate=%s, " +
                                "minRate=%s, pri=%s, burst=%s", entry.getValue().queueId(), entry.getValue().type(),
                        entry.getValue().dscp(), entry.getValue().maxRate(), entry.getValue().minRate(),
                        entry.getValue().priority(), entry.getValue().burst());
            }
        }
        });

    }
}
