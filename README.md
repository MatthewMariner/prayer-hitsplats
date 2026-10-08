# Prayer Hitsplats

A blue 0 on you means the attack missed, or a protection prayer stopped it. The game draws both
the same. This plugin recolours the 0s that land while you are praying against the attack, so a
blue 0 on you means a real miss.

## How it tells the attack

- A projectile that lands on you with the hit means ranged or magic, and either of those prayers
  counts. No projectile, with your attacker in melee reach, means melee: only Protect from Melee
  counts.
- The prayer is read as the server had it when the attack was made: for melee, on the hit's own
  tick, a click on that tick included; for a projectile, when it was fired, not when it lands. A
  flick that was up for the attack tints.

## Limits

- Ranged and magic look alike to the client, so the wrong one of those two prayers still tints.
- A tinted 0 means the matching prayer was up, not that it made the 0: a hit that would have
  missed anyway looks the same.
- The few bosses that check prayer when the hit lands, rather than when the attack begins, are
  judged at the attack.
- A magic attack with no projectile, from an attacker next to you, reads as melee.
- Only your own hitsplats. It uses no per-monster data and shows nothing before a hit lands.

## Settings

- **Colour**: the tint (default amber).
