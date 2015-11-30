package hu.unimiskolc.iit.distsys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VMManager.VMManagementException;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.AlterableResourceConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ConstantConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.io.NetworkNode.NetworkException;
import hu.mta.sztaki.lpds.cloud.simulator.io.Repository;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine.State;
import hu.mta.sztaki.lpds.cloud.simulator.io.StorageObject;
import hu.mta.sztaki.lpds.cloud.simulator.io.VirtualAppliance;
import hu.unimiskolc.iit.distsys.interfaces.FillInAllPMs;


public class PMFiller implements FillInAllPMs{

	
	@Override
	public void filler(IaaSService iaas, int vmCount) {
		// TODO Auto-generated method stub
	
		
		VirtualAppliance va = new VirtualAppliance("Virtaulappliance", 1, 0);
		VirtualMachine vm = new VirtualMachine(va);
		ResourceConstraints rc = new AlterableResourceConstraints(24.0, 20000.0, 4096);
		
		
		Timed.simulateUntilLastEvent();	
	
	
		for (PhysicalMachine pm : iaas.machines){
			//	pm.turnon();
			iaas.registerRepository(pm.localDisk);
			pm.localDisk.registerObject(va);
		//	for (int i = 0; i < vmCount; i++) {
			System.out.println(iaas.machines.size());
			double c = pm.freeCapacities.getRequiredCPUs();
			double p = pm.freeCapacities.getRequiredProcessingPower();
			long m = pm.freeCapacities.getRequiredMemory();
				
			System.out.println(c + ", " + p + ", " + m + ", " + iaas.machines.size());
				
	//		rc = new ConstantConstraints(c/10, p, m/10);
			
			try {
				vm = pm.requestVM(va, rc, pm.localDisk, iaas.machines.size())[0];
			} catch (VMManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NetworkException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			Timed.simulateUntilLastEvent();
				
			//	System.out.println(vm.getState());
//			System.out.println(vm.getResourceAllocation().allocated.getRequiredCPUs());
				System.out.println(vm.getResourceAllocation().getHost());
			//	System.out.println(vm.getResourceAllocation());
				Timed.simulateUntilLastEvent();
			//}
		}
	}
}
