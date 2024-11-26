#### modeling
##### **introduction**
To model the domain, the DDD (Domain-Driven Design) approach was applied, complemented by Event Storming. Event Storming is an excellent tool for supporting DDD processes, particularly during the exploratory and design phases. It facilitates the identification of domain events, the modeling of business processes, and the development of a shared language among all stakeholders.

Event Storming is created using draw.io, and the corresponding file is located in the designated folder(`./f17.drawio`).
Below, an exported image of the documentation is provided for reference:
![](images/f17-event_storming.drawio.svg)

##### **Domain Description**
Game is an online strategy game where players build and manage their villages while competing for dominance with other tribes. Each tribe can consist of multiple players, with one designated as the tribe leader. Players develop their villages by gathering resources, constructing buildings, and training armies.

Resources include wood, stone, and iron, and are gathered through resource buildings. Resources are also acquired by raiding other villages or through trade. A village can store only a limited amount of resources, based on its storage capacity.

Ranking system, where individual players and tribes are ranked by their accumulated battle points. Battle points are earned by winning battles against other players. Battles can either be offensive (initiated by a player) or defensive (when the player is attacked by another player).

Each player has a home village. Villages can be expanded by constructing new buildings or upgrading existing ones, which requires resources and time.

Tribes can form alliances with other tribes, creating larger coalitions. Wars are declared officially within the game, and all battles between warring tribes during the war period contribute to the war score.

Players can train different types of units and use them to attack.

Victory can be achieved by becoming the top-ranked tribe by battle points when the game world ends.

##### **Strategic Design (ES: Pig Picture & Process Modeling)**
During modeling, the following **definitions** were established as universal concepts, applicable across all bounded contexts within the domain:
- player - represent user in game;
- money - premium currency;
- gold coins - player can mind them in Academy, required to conquer villages;
- inventory - player container for items;
- village resources - resources in village;
- refresh resources - adds resources to village depending on the level of resource buildings;
- village builder - management of building constructions in village;
- village infrastructure - building in village;
- village army - village units
- village recruiter - management of recruit units in village;
- available unit - unit that belongs to the village and can be sent out
- blocked unit - unit that belongs to the village, but is unavailable, e.g., because of an expedition
- village loyalty - if loyalty falls below 0 village will be conquered;
- effects - active effects in village, effects can, for example, raise productions;
- village position - village position on map;
- tribe - group of players;

##### **Legend**:
![](images/legend.png)

##### **ES. Pig Picture, examples:**
- at first player related processes:
  ![](images/bp-player.png)
- resource related processes:
  ![](images/bp-resources.png)
- building related processes:
  ![](images/bp-building.png)

##### **ES. Process Modeling, examples:**
- player related processes:
  ![](images/pm-player.png)
- resource related processes:
  ![](images/pm-resources.png)
- building related processes:
  ![](images/pm-buildings.png)


##### **Tactical Design (ES: Pig Picture & Design Modeling)**
After documenting and describing the processes, the following **bounded contexts** were identified:

##### 1. player context
Implemented in Layered architectural patter as Active Record.

Objects:
- Player Entity

![](images/pm-1.png)

##### 2. player money context
Implemented in Port & Adapters architectural patter as Domain Model.

Aggregates:
- Player Money

![](images/pm-2.png)

##### 3. player gold coin  context
Implemented in Port & Adapters architectural patter as Domain Model.

Aggregates:
- Player Gold Coin

![](images/pm-3.png)

##### 4. inventory context
Implemented in Port & Adapters architectural patter as Domain Model.

Aggregates:
- Inventory

![](images/pm-4.png)

##### 5. village context
Implemented in Port & Adapters architectural patter as Domain Model.

Aggregates:
- Village

![](images/pm-5.png)

##### 6. village resource context
Implemented in Port & Adapters architectural patter as Domain Model.

Aggregates:
- Village Resource

![](images/pm-6.png)

##### 7. village builder context
Implemented in Port & Adapters architectural patter as Domain Model.

Objects:
- Village Builder Aggregate
- Village Infrastructure Aggregate
- Building Entity

![](images/pm-7.png)

##### 8. village army context
Implemented in Port & Adapters architectural patter as Domain Model.

Objects:
- Village Recruiter Aggregate
- Village Army Aggregate
- Unit Entity

![](images/pm-8.png)

##### 9. village loyalty context
Implemented in Port & Adapters architectural patter as Domain Model.

Aggregates:
- Village Loyalty

![](images/pm-9.png)

##### 10. village effects context
Implemented in Port & Adapters architectural patter as Domain Model.

Aggregates:
- Village effects

![](images/pm-10.png)

##### 11. map context
Implemented in Port & Adapters architectural patter as Domain Model.

Objects:
- Village Position Aggregate
- Map Entity

![](images/pm-11.png)

##### 12. army walk context
Implemented in Port & Adapters architectural patter as Domain Model.

Aggregates:
- Army Walk

![](images/pm-12.png)

##### 13. tribe context
Implemented in Port & Adapters architectural patter as Domain Model.

Aggregates:
- Tribe

![](images/pm-13.png)
