package data.scripts;

import com.fs.starfarer.api.impl.combat.BreachOnHitEffect;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;

public class sex_Boreblaster_OnHit implements OnHitEffectPlugin {

	public static float DAMAGE = 300;

	public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target,
					  Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
		if (!shieldHit && target instanceof ShipAPI) {
			BreachOnHitEffect.dealArmorDamage(projectile, (ShipAPI) target, point, DAMAGE);
		}
	}
}
