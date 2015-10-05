package hu.unimiskolc.iit.distsys;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VMManager.VMManagementException;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ConstantConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.io.NetworkNode.NetworkException;
import hu.mta.sztaki.lpds.cloud.simulator.io.VirtualAppliance;
import hu.unimiskolc.iit.distsys.interfaces.FillInAllPMs;


public class PMFiller implements FillInAllPMs{

	@Override
	public void filler(IaaSService iaas, int vmCount) {
		// TODO Auto-generated method stub
	//	PhysicalMachine pm[] = new PhysicalMachine[10];
		VirtualAppliance va = new VirtualAppliance("Virtaulappliance", 100.0, 0);

		Timed.simulateUntilLastEvent();
		ResourceConstraints rc = new ConstantConstraints(0.25, 10.0, 100);
		for (int i = 0; i < 10; i++){
			try {
				ResourceConstraints rc2 = iaas.machines.get(i).getCapacities();
				System.out.println(rc2.getRequiredCPUs());
				iaas.requestVM(va, rc, iaas.machines.get(i).localDisk, vmCount/iaas.machines.size());
			} catch (VMManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NetworkException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Timed.simulateUntilLastEvent();
		}
	}
}
