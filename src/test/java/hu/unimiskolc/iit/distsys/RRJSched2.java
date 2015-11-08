package hu.unimiskolc.iit.distsys;

import hu.mta.sztaki.lpds.cloud.simulator.DeferredEvent;
import hu.mta.sztaki.lpds.cloud.simulator.helpers.job.Job;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VMManager.VMManagementException;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine.State;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine.StateChange;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ConstantConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ConsumptionEventAdapter;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption;
import hu.mta.sztaki.lpds.cloud.simulator.io.NetworkNode.NetworkException;
import hu.mta.sztaki.lpds.cloud.simulator.io.Repository;
import hu.mta.sztaki.lpds.cloud.simulator.io.VirtualAppliance;
import hu.unimiskolc.iit.distsys.ComplexDCFJob;
import hu.unimiskolc.iit.distsys.ExercisesBase;
import hu.unimiskolc.iit.distsys.interfaces.BasicJobScheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import at.ac.uibk.dps.cloud.simulator.test.simple.cloud.VMTest;

public class RRJSched2 implements BasicJobScheduler, VirtualMachine.StateChange
{

	private IaaSService iaas;
	private Repository r;
	private VirtualAppliance va;
	private HashMap<VirtualMachine, Job> vmsWithPurpose = new HashMap<VirtualMachine, Job>();
	private HashMap<VirtualMachine, DeferredEvent> vmPool = new HashMap<VirtualMachine, DeferredEvent>();
	public static int[] aSucces = new int[TestHighAvailability.availabilityLevels.length];
	public static int[] aFailur = new int[TestHighAvailability.availabilityLevels.length];
	public static int[] CountJ = new int[TestHighAvailability.availabilityLevels.length];
	
	private ArrayList<Job> jobsToRun = new ArrayList<Job>();

	public void setupVMset(Collection<VirtualMachine> vms)
	{
		// ignore
	}

	public void setupIaaS(IaaSService iaas)
	{
		this.iaas = iaas;
		r = iaas.repositories.get(0);
		va = (VirtualAppliance) r.contents().iterator().next();

		for (int i = 0; i < TestHighAvailability.availabilityLevels.length; i++)
		{
			aFailur[i] = 0;
			aSucces[i] = 0;
			CountJ[i] = 0;
		}
	}

	public void handleJobRequestArrival(Job j)
	{		
		boolean restarted = Restart(j);
		
		if(!restarted)
			{
				jobsToRun.add(j);
				int index = getAvailabilityIndex(j);
				aSucces[index]++;
				
				CountJ[getAvailabilityIndex(j)]++;
			}
			
		ConstantConstraints cc = new ConstantConstraints(j.nprocs, ExercisesBase.minProcessingCap,
					ExercisesBase.minMem / j.nprocs);
			
			
		for (VirtualMachine vm : vmPool.keySet()) {
			if (vm.getResourceAllocation().allocated.getRequiredCPUs() >= j.nprocs) {
				vmPool.remove(vm).cancel();
				allocateVMforJob(vm, j);
				return;
			}
		}
		VirtualMachine vm;
		try {
			vm = iaas.requestVM(va, cc, r, 1)[0];
			vm.subscribeStateChange(this);
			vmsWithPurpose.put(vm, j);
		} catch (VMManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	}
	
	
	private void allocateVMforJob(final VirtualMachine vm, Job j) {
		try {
			((ComplexDCFJob) j).startNowOnVM(vm, new ConsumptionEventAdapter() {
				@Override
				public void conComplete() {
					super.conComplete();
					vmPool.put(vm, new DeferredEvent(
							ComplexDCFJob.noJobVMMaxLife - 1000) {
						protected void eventAction() {
							try {
								vmPool.remove(vm);
								vm.destroy(false);
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}
					});
				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public void stateChanged(final VirtualMachine vm, State oldState,
			State newState) {
		if (newState.equals(VirtualMachine.State.RUNNING)) {
			allocateVMforJob(vm, vmsWithPurpose.remove(vm));
			vm.unsubscribeStateChange(this);
		}
	}
	
	private int getAvailabilityIndex(Job j)
	{
		ComplexDCFJob job = (ComplexDCFJob) j;

		double availability = job.getAvailabilityLevel();
		int index = 0;

		for (int i = 0; i < TestHighAvailability.availabilityLevels.length; i++)
		{
			if (availability == TestHighAvailability.availabilityLevels[i])
			{
				index = i;
			}
		}
		
		return index;
	}
	
	private boolean Restart(Job job)
	{
		for(Job j : jobsToRun)
		{
			if(j.getId().equals(job.getId()))
			{
				return true;
			}
		}
		return false;
	}
	

}