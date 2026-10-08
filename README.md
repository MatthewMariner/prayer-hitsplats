# Prayer Hitsplats

A blue 0 on you means the attack missed, or a protection prayer stopped it; the game draws both
the same. This plugin recolours the 0s that land while you are praying against the attack, so a
blue 0 on you means a real miss.

## How a 0 is judged

- A projectile that lands on you with the hit means ranged or magic, and either of those prayers
  counts. No projectile, with something attacking you in melee reach, means melee, and only
  Protect from Melee counts. Anything else is taken as ranged or magic.
- Prayers are read as the server had them on the tick the attack was made, a click on that tick
  included: the hit's own tick for melee, the tick it was fired for a projectile.

## Limits

- Ranged and magic look alike to the client, so the wrong one of those two prayers still tints.
- A tinted 0 means the matching prayer was up, not that it made the 0: a hit that would have
  missed anyway looks the same.
- An attack the game judges when it lands, rather than when it is made, is still judged here at
  the start. A magic attack with no projectile, from something next to you, reads as melee.
- A 0 that arrives while four hitsplats already show on you is not tinted.
- Only your own hitsplats, after they land. No per-monster data.

## Settings

- **Colour**: the tint (default amber).
