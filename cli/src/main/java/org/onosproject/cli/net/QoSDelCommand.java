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
@Command(scope = "onos", name = "qos-del",
        description = "Qos Test.")
public class QoSDelCommand extends AbstractShellCommand {

    @Argument(index = 0, name = "ovsdbDeviceId", description = "the ovsdb TYPE id.", required = true,
            multiValued = false)
    String ovsdbDeviceId = null;

    @Argument(index = 1, name = "qos id", description = "qos id.", required = true,
            multiValued = false)
    String id = null;

   @Argument(index = 2, name = "port number", description = "the port number.", required = true,
            multiValued = false)
    String portnumber = null;


    @Override
    protected void doExecute() {
        DeviceService deviceService = DefaultServiceDirectory.getService(DeviceService.class);
        Device device = deviceService.getDevice(DeviceId.deviceId(ovsdbDeviceId));
        if (device == null) {
            log.error("{} isn't support config.", ovsdbDeviceId);
            return;
        }


        QueueConfigBehaviour queueConfig = device.as(QueueConfigBehaviour.class);
        QosConfigBehaviour qosConfig = device.as(QosConfigBehaviour.class);
        PortConfigBehaviour portConfig = device.as(PortConfigBehaviour.class);

	if(queueConfig.getQueues().stream().count()==0) print("No Queues (empty) ! ");
        else {    queueConfig.getQueues().stream().forEach(queue ->{
                String index[] = (String.valueOf(queue.queueId())).split("\\.");
                if(id.equals(index[1])) queueConfig.deleteQueue(queue.queueId());
            });
	}
	
	if(qosConfig.getQoses().stream().count()==0) print("No Qoses (empty) ! ");
        else {
	qosConfig.getQoses().stream().forEach(qos ->{
		print("Loop : Current QoS : %s / id : %s", String.valueOf(qos.qosId()), id);
		if(String.valueOf(qos.qosId()).equals(id)) {
			print("Condition met");
			qosConfig.deleteQoS(qos.qosId());
		}
		else print("Condition no met");
            });
        }
       portConfig.removeQoS(PortNumber.portNumber(Long.valueOf(portnumber), id));

    }
}
