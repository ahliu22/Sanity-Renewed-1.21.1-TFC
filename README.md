Sanity: Renewed — A Sanity Addon for TerraFirmaCraft

Sanity: Renewed brings the sanity mechanic from Don't Starve to Minecraft 1.21.1 (NeoForge), reworked as an addon for TerraFirmaCraft (TFC). Rain, thirst, hunger, extreme temperatures, darkness and monsters wear down your mind — while sleep, good food, a warm fire, pets and company help you stay sane.

Core Model  
- Your sanity is shown as 0%–100%; the lower it is, the more negative effects you suffer.  
- Every value can be tuned in config/sanity_renewed/default.toml: positive = restore sanity, negative = lose sanity. Passive values are per second, active values are per event.  
- Per-dimension overrides are supported via config/sanity_renewed/dimension/*.toml.  
- Creative and spectator players are unaffected.  

What Affects Sanity  
Passive (applied every second)  
- Rain / water: −0.2/s while rained on or standing in vanilla water, TFC river water or TFC salt water; +0.2/s while soaking in a TFC hot spring (spring water takes priority).  
- Hunger: −0.2/s at food level ≤ 8.  
- Thirst: −0.2/s at TFC thirst ≤ 40%.  
- Extreme temperature: −0.15/s when the instant TFC environment temperature is above 25°C or below 5°C (thresholds and rates configurable).  
- Darkness: −0.15/s at light level ≤ 4.  
- Light: +0.0/s at light level ≥ 10.  
- Nearby monsters: −0.1/s with a visible monster within 8 blocks; doubled if one is targeting you.  
- Pet company: +0.05/s for a visible TFC animal with familiarity above 0.3 within 8 blocks.  
- Sane company: +0.05/s near a player with sanity above 50%.  
- Insane company: −0.12/s near a player with sanity at or below 50%.  
- Jukebox: +0.08/s for a pleasant song within 60 blocks; −0.11/s for an unsettling one (discs 5/11/13).  
- Lit TFC heat sources: firepit, grill, pot, stove, stove pot (lit=true) and charcoal forge (heat_level>0) give +0.1/s when visible within 4 blocks.  
- Garland: worn on the head, restores +0.1/s and reduces all sanity drain by 8%; consumes 1 durability every 3 seconds (2 in rain/water).  
Active (one-time events)  
- Sleeping through the night: +50.  
- Earning an advancement: +10.  
- TFC animals mating: +2 when they mate within 4 blocks of the player (shared cooldown configurable).  
- Eating: TFC foods grant sum of all five nutrients × 0.5 sanity; if a data pack rule matches, the value is multiplied by its modifier (multiple rules multiply together). Item tooltips show the exact sanity value (no cooldown by default, so every meal counts).  
- Taking damage: −1 per point of damage.  
- Killing an inner entity: +3, and increases the spawn count and frequency of future inner entities.  
- Configured item exceptions still apply: rotten flesh/pufferfish/poisonous potato/spider eye −5, chorus fruit −3, ender pearl −1; honey bottle +6, golden carrot +7, golden apple +8, enchanted golden apple +13.  

Data Pack Food Rules  
- Define rules in data/<namespace>/sanity/food/*.json as {"ingredient": {"item": "..."} or {"tag": "..."}, "modifier": -0.5}.  
- The base value is nutrient sum × 0.5, then multiplied by every matching rule modifier. Built-in example: sandwiches in the sanity_renewed:badfoods tag have a −0.5 modifier.  

Effects of Low Sanity (all thresholds configurable)  
- Sanity ≤ 50%: the sanity indicator and inner monologue begin to twitch; the heartbeat starts fading in.  
- Sanity ≤ 45%: insanity ambience starts fading in.  
- Sanity ≤ 40%: screen post-processing begins (distortion, desaturation, chromatic aberration); inner monologue, fake footsteps and hallucination sounds appear; the blood tendrils overlay shows up while losing sanity.  
- Sanity ≤ 25%: inner entities become visible.  
- Sanity ≤ 20%: inner entities start spawning around you; post-processing, ambience and heartbeat reach maximum intensity.  
- Sanity ≤ 15%: inner entities actively hunt you and can be fought back; they prefer the most insane player on the server.  
Inner Entities: the Rotting Stalker (10 HP, 8 damage, fast) and the Sneaking Terror (14 HP, 16 damage, knockback resistant). They cannot push players and drop no experience, but killing one restores sanity. They are visible at ≤ 25%, spawn at ≤ 20%, and become hostile/attackable at ≤ 15% (all thresholds and stats are configurable).  
High sanity grants no direct stat bonuses — its benefit is immunity to every negative effect above, plus normal villager trading, animal interaction and social play.  

Misc  
- New command /sanity get [players] to check sanity, alongside /sanity set, /sanity add and /sanity config reload.  
- Removed: enderman staring, being stuck in blocks, hurting passive animals, pet death, lightning strikes, dimension changes, farmland trampling, breaking infested blocks, villager trading, shearing, egg throwing, flower potting and fishing.  


Sanity: Renewed — 群峦传说附属「理智」模组

Sanity: Renewed 是一个将《饥荒》式理智机制带入 Minecraft 1.21.1（NeoForge）的模组，现已改造为群峦传说附属。渴了、冷了、淋雨了、在黑暗里遇到怪物，都会侵蚀你的心智；而睡个好觉、吃顿好饭、围着篝火、与宠物和同伴相伴，则能让理智恢复。

核心模型  
- 玩家面板上的 san 值为 *0%～100%*；数值越低，受到的负面影响越多。  
- 所有增减幅度均可在配置文件（config/sanity_renewed/default.toml）中修改，正数=回复 san，负数=损失 san；被动数值单位为“每秒”，主动数值单位为“每次事件”。  
- 支持按维度覆盖配置（config/sanity_renewed/dimension/*.toml）。  
- 创造/旁观模式不受任何影响。

影响 san 值的因素  
被动（每秒持续结算）  
- 淋雨/泡水：下雨或泡在原版水、群峦河水、群峦咸水中 −0.2/s；泡在群峦温泉（spring water）中 +0.2/s（温泉优先判定）。  
- 饥饿：饱食度 ≤ 8 时 −0.2/s。  
- 口渴：群峦口渴值 ≤ 40% 时 −0.2/s。  
- 极端温度：群峦瞬时环境温度 > 25°C 或 < 5°C 时 −0.15/s（冷暖阈值和速度均可配置）。  
- 黑暗：亮度 ≤ 4 时 −0.15/s。  
- 光明：亮度 ≥ 10 时 +0.0/s。  
- 附近怪物：8 格内有可见怪物 −0.1/s；被怪物锁定为攻击目标时翻倍。  
- 宠物陪伴：8 格内有亲密度 > 0.3 的群峦驯化动物（且有视线）+0.05/s。  
- 理智玩伴：8 格内有 san > 50% 的玩家 +0.05/s。  
- 疯狂玩伴：8 格内有 san ≤ 50% 的玩家 −0.12/s。  
- 唱片机：60 格内播放悦耳唱片 +0.08/s；播放 5/11/13 号不祥唱片 −0.11/s。  
- 点燃的群峦火源：篝火、烤架、陶锅、炉灶、炉灶锅（lit=true）以及木炭炉（heat_level>0），4 格内可见时 +0.1/s。  
- 花环：佩戴在头盔栏时 +0.1/s，并使所有掉 san 效果减弱 8%；每 3 秒消耗 1 点耐久（淋雨/泡水时 2 点）。  
主动（事件触发，一次性）  
- 睡整觉：跳过夜晚 +50。  
- 获得成就：+10。  
- 群峦动物交配：动物在玩家 4 格内交配 +2（共享冷却可配置）。  
- 吃东西：食用群峦食物获得 五种营养总和 × 0.5 的 san；若食物被数据包规则匹配，再乘以规则中的 modifier（可叠加多个规则）。物品悬浮提示会显示该食物实际的 san 增减值（默认没有冷却，每份食物都生效）。  
- 受伤：每点伤害 −1。  
- 击杀幻觉怪物：+3，并提高后续幻觉怪物的生成数量与频率。  
- 配置物品特例：食用配置列表中的物品仍按配置生效（腐肉/河豚/毒马铃薯/蜘蛛眼 −5，紫颂果 −3，末影珍珠 −1；蜂蜜瓶 +6，金胡萝卜 +7，金苹果 +8，附魔金苹果 +13）。  

数据包自定义食物规则  
- 在 data/<命名空间>/sanity/food/*.json 中定义规则，格式：{"ingredient": {"item": "..."} 或 {"tag": "..."}, "modifier": -0.5}。  
- 食用时先算 营养总和 × 0.5，再乘上所有匹配规则的 modifier。模组自带示例：sanity_renewed:badfoods 标签内的三明治 modifier 为 −0.5。

低 san 对玩家的影响（阈值全部可配置）  
- *san ≤ 50%*：HUD 理智指示条与内心独白开始抖动；心跳声开始淡入。  
- *san ≤ 45%*：疯癫环境音开始淡入。  
- *san ≤ 40%*：屏幕后处理开始（画面扭曲、去色、色差）；出现内心独白、身后假脚步声和幻听音效；血丝覆盖层出现（在掉 san 时触发）。  
- *san ≤ 25%*：开始能看见幻觉怪物。  
- *san ≤ 20%*：幻觉怪物开始在玩家周围生成；后处理、疯癫环境音、心跳声达到最大强度。  
- *san ≤ 15%*：幻觉怪物正式锁定并攻击你，同时你也可以攻击它们；它们会优先追杀全服最疯狂的玩家。  
幻觉怪物：包括腐烂潜行者（10 血、8 伤害、速度快）与潜伏恐魔（14 血、16 伤害、抗击退）。它们不推动玩家、不掉经验；击杀会回复 san。幻觉怪物在 san ≤ 25% 时可见、san ≤ 20% 时生成、san ≤ 15% 时才可被攻击并主动索敌（各项阈值与怪物参数均在配置中）。  
高 san 没有额外属性增益，但会完全免疫上述所有负面效果，并保持村民交易、动物互动与正常社交等行为。  

其他
- 新增 /sanity get [玩家] 查看当前 san，/sanity set、/sanity add、/sanity config reload 管理命令。  
- 已删除：注视末影人、卡在方块、伤害和平动物、宠物死亡、被雷劈、跨维度、踩坏耕地、破坏虫蚀方块、村民交易、剪羊毛、扔鸡蛋、种花盆、钓鱼等旧机制。
