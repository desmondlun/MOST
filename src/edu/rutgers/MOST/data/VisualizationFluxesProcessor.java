package edu.rutgers.MOST.data;

import java.util.ArrayList;
import java.util.Vector;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.presentation.PathwaysFrameConstants;

public class VisualizationFluxesProcessor {
	// need fluxes to get max and secondary max flux
	// absolute value of infinite fluxes are approximately equal to maximum upper bound (within 95%)
	// secondary max flux is largest non-infinite flux
	private ArrayList<Double> fluxes = new ArrayList<Double>();
	private double maxFlux;
	private double secondaryMaxFlux;
	
	public ArrayList<Double> getFluxes() {
		return fluxes;
	}
	public void setFluxes(ArrayList<Double> fluxes) {
		this.fluxes = fluxes;
	}
	public double getMaxFlux() {
		return maxFlux;
	}
	public void setMaxFlux(double maxFlux) {
		this.maxFlux = maxFlux;
	}
	public double getSecondaryMaxFlux() {
		return secondaryMaxFlux;
	}
	public void setSecondaryMaxFlux(double secondaryMaxFlux) {
		this.secondaryMaxFlux = secondaryMaxFlux;
	}
	
	public void processReactions(Vector<SBMLReaction> rxns) {
		double maxUpperBound = 0;
		for (int i = 0; i < rxns.size(); i++) {
			fluxes.add(rxns.get(i).getFluxValue());
			if (rxns.get(i).getUpperBound() > maxUpperBound) {
				maxUpperBound = rxns.get(i).getUpperBound();
			}
		}
		maxFlux = maxUpperBound;
	}
	
	public void processFluxes() {
		for (int j = 0; j< fluxes.size(); j++) {
			if (Math.abs(fluxes.get(j)) <= PathwaysFrameConstants.INFINITE_FLUX_RATIO*maxFlux) {
				if (Math.abs(fluxes.get(j)) > secondaryMaxFlux) {
					secondaryMaxFlux = Math.abs(fluxes.get(j));
				}
			}
		}
		LocalConfig.getInstance().setFluxes(fluxes);
		LocalConfig.getInstance().setMaxFlux(maxFlux);
		LocalConfig.getInstance().setSecondaryMaxFlux(secondaryMaxFlux);
	}

}
