package data.scripts;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class sex_MineFeedStats extends BaseShipSystemScript {

	public static final float ROF_BONUS = 1f;
	
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		
		float mult = 1f + ROF_BONUS * effectLevel;
		stats.getMissileRoFMult().modifyMult(id, mult);
		
//		ShipAPI ship = (ShipAPI)stats.getEntity();
//		ship.blockCommandForOneFrame(ShipCommand.FIRE);
//		ship.setHoldFireOneFrame(true);
	}
	public void unapply(MutableShipStatsAPI stats, String id) {
		stats.getMissileRoFMult().unmodify(id);
	}
	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		float mult = 1f + ROF_BONUS * effectLevel;
		float bonusPercent = (int) ((mult - 1f) * 100f);
		if (index == 0) {
			return new StatusData("Missile rate of fire +" + (int) bonusPercent + "%", false);
		}
		return null;
	}
}
