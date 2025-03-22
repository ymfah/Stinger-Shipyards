package data.scripts;

import java.awt.Color;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;

public class sex_CIWS_OnHit implements OnHitEffectPlugin {


	public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target,
					  Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
		float extraDamageMissile = 45f;
		float extraDamageFighter = 25f;

        if(target instanceof MissileAPI){
            engine.applyDamage(target, 
                    point,
					extraDamageMissile,
                    DamageType.HIGH_EXPLOSIVE, 
                    0f, 
                    false, 
                    false, 
                    projectile.getSource());
			}
		if(target instanceof ShipAPI && ((ShipAPI) target).isFighter()){
			engine.applyDamage(target,
					point,
					extraDamageFighter,
					DamageType.HIGH_EXPLOSIVE,
					0f,
					false,
					false,
					projectile.getSource());
		}
		}
	}
