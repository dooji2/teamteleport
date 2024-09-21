![What is TeamTeleport?](https://cdn.modrinth.com/data/cached_images/43c5622465210df6e427673a336fb566c6289d51_0.webp)

**TeamTeleport** is a Minecraft plugin designed to facilitate team-based teleportation. It allows players to teleport their entire team or groups of players within a specified radius, creating a streamlined teleportation process. The plugin is customizable, providing configurable teleport delays, timeouts, and request handling.

---

![How to use TeamTeleport?](https://cdn.modrinth.com/data/cached_images/bbdfa031a2edd68311a08f8ab9c3119c16dd728c_0.webp)

**TeamTeleport** supports various commands for team and radius-based teleportation. Here are the commands and their usage:

<details>
  <summary><strong>Basic Commands</strong></summary>

```bash
/teamtp <playerName>
```
*Teleports the command sender and their team (if applicable) to the location of the specified player.*

```bash
/teamtp <x> <y> <z>
```
*Teleports the command sender and their team to specific coordinates.*
  
</details>

<details>
  <summary><strong>Team Commands</strong></summary>

```bash
/teamtp team <teamName> <playerName>
```
*Teleports all players from the specified team to the target player.*

```bash
/teamtp team <teamName> <x> <y> <z>
```
*Teleports all players from the specified team to the provided coordinates.*

</details>

<details>
  <summary><strong>Radius Commands</strong></summary>

```bash
/teamtp radius <radius> <x> <y> <z>
```
*Teleports all team members within the specified radius of the command sender to the provided coordinates.*

```bash
/teamtp radius <radius>
```
*Teleports the sender's team within a certain radius to them.*

```bash
/teamtp team <teamName> radius <radius> <playerName | x y z>
```
*Teleports all team members within the radius to either a player or a set of coordinates.*

</details>

<details>
  <summary><strong>Request Commands</strong></summary>

```bash
/teamtp allow requests
```
*Allows the player to accept teleport requests from other team members.*

```bash
/teamtp deny requests
```
*Denies all non-OP teleport requests for the player.*

```bash
/teamtp accept
```
*Approves a teleport request sent to the player by a teammate.*

```bash
/teamtp deny
```
*Denies a teleport request sent to the player.*

</details>

**TeamTeleport** also supports permissions:
- `teamteleport.admin` - Allows players to use teleport commands without approval. Players with OP can already do this.
- `teamteleport.use` - Allows regular players to use teleport commands with approval.

### Configuration
`config.yml` lets server owners change the teleport delay, request timeout and command time out for non-OP players. It's worth noting they are all expected to be in seconds.

---

![Compatibility](https://cdn.modrinth.com/data/cached_images/f23e2224283de81cf19e7e163d77188fa1ae9e87_0.webp)

**TeamTeleport** is compatible with **Paper**, **Spigot**, **Purpur** and **Bukkit**, from **Minecraft 1.20** upwards.

For now, the mod doesn't have any integration with existing team plugins, and as such everything is based on the vanilla teams system. Feel free to leave suggestions on the GitHub Issues page regarding possible integrations!
