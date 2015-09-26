package hu.unimiskolc.iit.distsys;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine.ResourceAllocation;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.AlterableResourceConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ConstantConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.io.VirtualAppliance;

public class VMC implements VMCreationApproaches{

	public void directVMCreation() throws Exception{
		PhysicalMachine pm1 = ExercisesBase.getNewPhysicalMachine();
		pm1.turnon();
		Timed.simulateUntilLastEvent();
		VirtualAppliance va = new VirtualAppliance("Virtaulappliance", 34.0, 0);
		ResourceConstraints rc1 = new ConstantConstraints(0.25, 10.0, 100);
		pm1.localDisk.registerObject(va);
		pm1.requestVM(va, rc1, pm1.localDisk, 2);
		Timed.simulateUntilLastEvent();
	}

	public void twoPhaseVMCreation() throws Exception{
		PhysicalMachine pm1 = ExercisesBase.getNewPhysicalMachine();
		pm1.turnon();
		Timed.simulateUntilLastEvent();
		VirtualAppliance va = new VirtualAppliance("Virtaulappliance", 34.0, 0);
		VirtualMachine vm1 = new VirtualMachine(va);
		VirtualMachine vm2 = new VirtualMachine(va);
		ResourceAllocation ra1 = pm1.allocateResources(new ConstantConstraints(0.25, 10.0, 100), false, 2);
		ResourceAllocation ra2 = pm1.allocateResources(new ConstantConstraints(0.25, 10.0, 100), false, 2);
		pm1.localDisk.registerObject(va);
		pm1.deployVM(vm1, ra1, pm1.localDisk);
		pm1.deployVM(vm2, ra2, pm1.localDisk);
		Timed.simulateUntilLastEvent();
	}

	public void indirectVMCreation() throws Exception{
		IaaSService iaaSService = ExercisesBase.getNewIaaSService();
		PhysicalMachine pm1 = ExercisesBase.getNewPhysicalMachine();
		pm1.turnon();
		Timed.simulateUntilLastEvent();
		VirtualAppliance va = new VirtualAppliance("Virtaulappliance", 34.0, 0);
		pm1.localDisk.registerObject(va);
		iaaSService.registerHost(pm1);
		iaaSService.registerRepository(pm1.localDisk);
		ResourceConstraints rc = new ConstantConstraints(0.25, 10.0, 100);
		iaaSService.requestVM(va, rc, pm1.localDisk, 2);
		Timed.simulateUntilLastEvent();
	}

	public void migratedVMCreation() throws Exception{
		PhysicalMachine pm1 = ExercisesBase.getNewPhysicalMachine();
		pm1.turnon();
		PhysicalMachine pm2 = ExercisesBase.getNewPhysicalMachine();
		pm2.turnon();
		Timed.simulateUntilLastEvent();
		VirtualAppliance va1 = new VirtualAppliance("Virtaulappliance1", 34.0, 0);
		
		ResourceConstraints rc1 = new ConstantConstraints(0.25, 10.0, 100);
		
		pm1.localDisk.registerObject(va1);
	
		pm1.requestVM(va1, rc1, pm1.localDisk, 1);
		//pm2.requestVM(va2, rc2, pm1.localDisk, 2);
		Timed.simulateUntilLastEvent();
	}
}

