package hu.unimiskolc.iit.distsys;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ConstantConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.io.VirtualAppliance;


public class ElosztottMain {
	public void indirectVMCreation100() throws Exception{
		IaaSService iaaSService = ExercisesBase.getNewIaaSService();
		PhysicalMachine pm[] = new PhysicalMachine[10];
		VirtualAppliance va = new VirtualAppliance("Virtaulappliance", 10.0, 0);
		
		for (int i = 0; i < 10; i++){
			pm[i] = ExercisesBase.getNewPhysicalMachine();
			pm[i].turnon();
			pm[i].localDisk.registerObject(va);
			iaaSService.registerHost(pm[i]);
			iaaSService.registerRepository(pm[i].localDisk);
		}
		Timed.simulateUntilLastEvent();
		ResourceConstraints rc = new ConstantConstraints(0.25, 10.0, 100);
		for (int i = 0; i < 10; i++){
			iaaSService.requestVM(va, rc, pm[i].localDisk, 10);
			Timed.simulateUntilLastEvent();
		}
	}
}
