# Prayer Hitsplats

A blue 0 on you means the attack missed, or a protection prayer stopped it; the game draws both
the same. This plugin recolours the 0s that land while you are praying against the attack, so a
blue 0 on you means a real miss.

## How a 0 is judged

- A projectile that lands on you with the hit counts if any protection prayer was up when it was
  fired or when it landed. A monster judges at one moment or the other, and the plugin does not
  know which, nor the attack's style.
- No projectile, with something attacking you in melee reach, means melee, and only Protect from
  Melee counts. Anything else is taken as ranged or magic, and either of those prayers counts.
- Prayers are read as the server had them, a click on that tick included: the hit's own tick for
  a hit with no projectile, both the tick it was fired and the tick it landed for a projectile.

## Limits

- Ranged and magic look alike to the client, so the wrong one of those two prayers still tints.
- A tinted 0 means the matching prayer was up, not that it made the 0: a hit that would have
  missed anyway looks the same.
- A projectile that misses on its own while only the wrong protection prayer is up also tints.
- A magic attack with no projectile, from something next to you, reads as melee.
- A 0 that arrives while four hitsplats already show on you is not tinted.
- Only your own hitsplats, after they land. No per-monster data.

## Settings

- **Colour**: the tint (default amber).
