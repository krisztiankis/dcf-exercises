package hu.unimiskolc.iit.distsys;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.helpers.job.Job;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VMManager.VMManagementException;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine.State;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine.StateChange;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ConstantConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.io.NetworkNode.NetworkException;
import hu.mta.sztaki.lpds.cloud.simulator.io.VirtualAppliance;
import hu.unimiskolc.iit.distsys.interfaces.BasicJobScheduler;

public class RRJSched implements BasicJobScheduler {
	

 

	private IaaSService iaas;

	@Override
	public void setupVMset(Collection<VirtualMachine> vms) {
				
	}

	@Override
	public void setupIaaS(IaaSService iaas) {
		this.iaas = iaas;		
		
	}

	@Override
	public void handleJobRequestArrival(Job j) {
		
		VirtualAppliance va = new VirtualAppliance("Virtaulappliance", 34.0, 0);
		try {
			for (int i = 0; i < iaas.machines.size(); i++) {
				PhysicalMachine pm = iaas.machines.get(i); 
				
			
				iaas.registerRepository(pm.localDisk);
				pm.localDisk.registerObject(va);
				ResourceConstraints rc = new ConstantConstraints(0.25, 10.0, 100);
				VirtualMachine vm = iaas.requestVM(va, rc, pm.localDisk, 2)[0];
				Timed.simulateUntilLastEvent();
				System.out.println(vm.getState());
				
				
			//	CusConEvent completionEvent = new CusConEvent();
				
				ComplexDCFJob cj = new ComplexDCFJob(j.getId(),
						j.getSubmittimeSecs(),
						j.getQueuetimeSecs(),
						j.getExectimeSecs(),
						j.nprocs,
						j.perProcCPUTime,
						j.usedMemory,
						j.user,
						j.group,
						j.executable,
						j.preceding,
						j.thinkTimeAfterPreceeding);
				
				
				cj.startNowOnVM(vm, VMStateChange.this.);
				
			}
			
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (VMManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
	

class VMStateChange implements StateChange{

	@Override
	public void stateChanged(VirtualMachine vm, State oldState, State newState) {
		
		if (oldState != vm.getState().RUNNING){
			CusConEvent completionEvent = new CusConEvent();
			newState = vm.getState().RUNNING;
			oldState = newState;
			
			
		}
		
	}
	
}

