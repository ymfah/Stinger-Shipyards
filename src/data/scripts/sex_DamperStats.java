package data.scripts;

import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class sex_DamperStats extends BaseShipSystemScript {

	private static Map mag = new HashMap();
	static {
		mag.put(HullSize.FIGHTER, 0.33f);
		mag.put(HullSize.FRIGATE, 0.33f);
		mag.put(HullSize.DESTROYER, 0.33f);
		mag.put(HullSize.CRUISER, 0.5f);
		mag.put(HullSize.CAPITAL_SHIP, 0.5f);
	}
	
	protected Object STATUSKEY1 = new Object();
	
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		effectLevel = 0.3f;
		
		float mult = (Float) mag.get(HullSize.CRUISER);
		if (stats.getVariant() != null) {
			mult = (Float) mag.get(stats.getVariant().getHullSize());
		}
		stats.getHullDamageTakenMult().modifyMult(id, effectLevel);
		stats.getArmorDamageTakenMult().modifyMult(id, effectLevel);
		
		
		ShipAPI ship = null;
		boolean player = false;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
			player = ship == Global.getCombatEngine().getPlayerShip();
		}
		if (player) {
			ShipSystemAPI system = getDamper(ship);
			if (system != null) {
				float percent = (1f - mult) * effectLevel * 100;
				Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY1,
					system.getSpecAPI().getIconSpriteName(), system.getDisplayName(),
					(int) Math.round(percent) + "% less damage taken", false);
			}
		}
	}
	
	public static ShipSystemAPI getDamper(ShipAPI ship) {
		ShipSystemAPI system = ship.getPhaseCloak();
		if (system != null && system.getId().equals("damper")) return system;
		if (system != null && system.getId().equals("damper_omega")) return system;
		if (system != null && system.getSpecAPI() != null && system.getSpecAPI().hasTag(Tags.SYSTEM_USES_DAMPER_FIELD_AI)) return system;
		return ship.getSystem();
	}
	
	public void unapply(MutableShipStatsAPI stats, String id) {
		stats.getHullDamageTakenMult().unmodify(id);
		stats.getArmorDamageTakenMult().unmodify(id);
	}
}
