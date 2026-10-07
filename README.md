# Prayer Hitsplats

A blue 0 on you means the attack missed, or a protection prayer stopped it. The game draws both
the same. This plugin recolours the 0s that land while you are praying against the attack, so a
blue 0 on you means a real miss.

## How it tells the attack

- A projectile that lands on you with the hit means ranged or magic. Either of those prayers
  counts, because the client cannot tell the two apart.
- No projectile, with your attacker standing in melee reach, means melee: only Protect from Melee
  counts.

## Limits

- A tinted 0 means you were praying against the attack, not that the prayer saved you. The game
  shows 0 either way, so a hit that would have missed anyway looks the same.
- A magic attack with no projectile, from an attacker next to you, reads as melee.
- Only your own hitsplats. It uses no per-monster data and shows nothing before a hit lands.

## Settings

- **Colour**: the tint (default amber).
