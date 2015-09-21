package hu.unimiskolc.iit.distsys;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ConstantConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.io.VirtualAppliance;

public class VMC implements VMCreationApproaches{

	public void directVMCreation() throws Exception{
		PhysicalMachine pm1 = ExercisesBase.getNewPhysicalMachine();
		pm1.turnon();
		Timed.simulateUntilLastEvent();
		VirtualAppliance va = new VirtualAppliance("Virtaulappliance1", 34.0, 0);
		ResourceConstraints rc1 = new ConstantConstraints(0.25, 10.0, 100);
		pm1.localDisk.registerObject(va);
		pm1.requestVM(va, rc1, pm1.localDisk, 2);
		Timed.simulateUntilLastEvent();
	}

	public void twoPhaseVMCreation() throws Exception{
		PhysicalMachine pm1 = ExercisesBase.getNewPhysicalMachine();
		pm1.turnon();
		Timed.simulateUntilLastEvent();
		VirtualAppliance va = new VirtualAppliance("Virtaulappliance1", 34.0, 0);
		ResourceConstraints rc1 = new ConstantConstraints(0.25, 10.0, 100);
		ResourceConstraints rc2 = new ConstantConstraints(0.15, 5.0, 100);
		pm1.localDisk.registerObject(va);
		
		Timed.simulateUntilLastEvent();	
	}

	public void indirectVMCreation() throws Exception{
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

