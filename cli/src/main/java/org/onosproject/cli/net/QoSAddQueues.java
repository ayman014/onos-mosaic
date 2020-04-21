package org.onosproject.cli.net;
import org.onosproject.cli.net.QoSAddClass;
import org.onosproject.cli.AbstractShellCommand;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;


@Service
@Command(scope = "onos", name = "queue-scenario",
        description = "Qos Test.")
public class QoSAddQueues extends AbstractShellCommand {

    @Override
    protected void doExecute() {
        QoSAddClass s1q0 = new QoSAddClass("s1-eth2", "2", "12000",       "1000000", "500",      7, 0);  s1q0.execute();
        QoSAddClass s1q1 = new QoSAddClass("s1-eth2", "2", "800000",     "1000000", "32000",  1, 10); s1q1.execute();
        QoSAddClass s1q2 = new QoSAddClass("s1-eth2", "2", "150000",     "1000000", "8000",  2, 18); s1q2.execute();
        QoSAddClass s1q3 = new QoSAddClass("s1-eth2", "2", "38000",      "1000000", "1000",   3, 26); s1q3.execute();

        QoSAddClass s2q0 = new QoSAddClass("s2-eth2", "2", "12000",      "1000000", "500",   7, 0); s2q0.execute();
        QoSAddClass s2q1 = new QoSAddClass("s2-eth2", "2", "800000",    "1000000", "32000", 1, 10); s2q1.execute();
        QoSAddClass s2q2 = new QoSAddClass("s2-eth2", "2", "150000",    "1000000", "8000", 2, 18); s2q2.execute();
        QoSAddClass s2q3 = new QoSAddClass("s2-eth2", "2", "38000",      "1000000", "1000", 3, 26); s2q3.execute();

        QoSAddClass s3q0 = new QoSAddClass("s3-eth1", "1", "12000",      "1000000", "500",   7, 0); s3q0.execute();
        QoSAddClass s3q1 = new QoSAddClass("s3-eth1", "1", "800000",    "1000000", "32000", 1, 10); s3q1.execute();
        QoSAddClass s3q2 = new QoSAddClass("s3-eth1", "1", "150000",    "1000000", "8000", 2, 18); s3q2.execute();
        QoSAddClass s3q3 = new QoSAddClass("s3-eth1", "1", "38000",     "1000000", "1000", 3, 26); s3q3.execute();


        QoSAddClass s1q0R = new QoSAddClass("s1-eth1", "1", "12000",     "1000000","500",       7, 0); s1q0R.execute();
        QoSAddClass s1q1R = new QoSAddClass("s1-eth1", "1", "800000", "1000000", "32000", 1, 10); s1q1R.execute();
        QoSAddClass s1q2R = new QoSAddClass("s1-eth1", "1", "150000", "1000000", "8000", 2, 18); s1q2R.execute();
        QoSAddClass s1q3R = new QoSAddClass("s1-eth1", "1", "38000", "1000000", "1000",3, 26); s1q3R.execute();

        QoSAddClass s2q0R = new QoSAddClass("s2-eth1", "1", "12000", "1000000", "500",  7, 0); s2q0R.execute();
        QoSAddClass s2q1R = new QoSAddClass("s2-eth1", "1", "800000", "1000000", "32000", 1, 10); s2q1R.execute();
        QoSAddClass s2q2R = new QoSAddClass("s2-eth1", "1", "150000", "1000000", "8000", 2, 18); s2q2R.execute();
        QoSAddClass s2q3R = new QoSAddClass("s2-eth1", "1", "38000",  "1000000", "1000", 3, 26); s2q3R.execute();

        QoSAddClass s3q0R = new QoSAddClass("s3-eth2", "2", "12000", "1000000", "500",  7, 0); s3q0R.execute();
        QoSAddClass s3q1R = new QoSAddClass("s3-eth2", "2", "800000", "1000000", "32000", 1, 10); s3q1R.execute();
        QoSAddClass s3q2R = new QoSAddClass("s3-eth2", "2", "150000", "1000000", "8000", 2, 18); s3q2R.execute();
        QoSAddClass s3q3R = new QoSAddClass("s3-eth2", "2", "38000", "1000000", "1000", 3, 26); s3q3R.execute();
        }
}


