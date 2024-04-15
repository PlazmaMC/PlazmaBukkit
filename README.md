<div align="center">

[![image](src/resources/title-400.png)](https://github.com/PlazmaMC/Plazma)

### A Server Platform for Minecraft: Java Edition based on [Paper](https://github.com/PaperMC/Paper), which provides the fastest performance

[![Discord](https://api.plazmamc.org/internal/cdn/discord)](https://plazmamc.org/discord)
[![License](https://api.plazmamc.org/internal/cdn/license)](LICENSE.md)
[![Downloads](https://api.plazmamc.org/internal/cdn/downloads)](https://plazmamc.org/downloads)
[![Stargazers](https://api.plazmamc.org/internal/cdn/stars)](https://github.com/PlazmaMC/Plazma/stargazers)

[![Upstream Status](https://api.plazmamc.org/internal/cdn/upstreams)](https://github.com/PlazmaMC/AlwaysUpToDate/actions)
[![Forks](https://api.plazmamc.org/internal/cdn/forks)](https://github.com/PlazmaMC/Plazma/forks)
[![Watchers](https://api.plazmamc.org/internal/cdn/watches)](https://github.com/PlazmaMC/Plazma/watchers)

[^warn]: In most cases, it works fine, but it can sometimes cause errors, so it should be used after sufficient testing.<br>대부분의 경우 정상적으로 작동하지만 때때로 오류를 일으킬 수 있으므로, 충분한 테스트를 거친 후 사용해야 합니다.
[^missing]: Some patches are still in development. There may be differences in functionality compared to stable versions.<br>일부 패치가 아직 개발중이므로, 안정 버전과 기능 차이가 있을 수 있습니다.

### [📑 Document](https://docs.plazmamc.org/) | [⬇️ Downloads](https://plazmamc.org/downloads)

#

</div>

> [!IMPORTANT]
Not long ago, the paper modified the code base overall (decompiler change), and most patches of Plazma are not currently applied.
And, both developers are currently very busy, and Plazma updates continue to be delayed.<br/><br/>
얼마 전 Paper에서 코드 기반을 전반적으로 수정하면서(디컴파일러 변경) 현재 Plazma의 대부분의 패치가 적용되지 않고 있습니다.
그리고, 두 개발자 모두 현재 굉장히 바쁜 상태로, Plazma 업데이트가 계속해서 지연되고 있습니다.<br/><br/>
We will proceed with the 1.20.4 update as soon as possible within May, and after the 1.20.5 release, we will update as soon as possible.   
최대한 5월 내에는 1.20.4 업데이트를 진행하고, 1.20.5 릴리스 후 최대한 빨리 업데이트 할 수 있도록 하겠습니다.

> [!WARNING]
Plazma may cause **<u>unexpected problems</u>**, so be sure to test it thoroughly before using it on a public server.<br>
플라즈마는 **<u>예기치 못한 문제를</u>** 일으킬 수 있으므로, 공개 서버에서 사용하기 전 충분한 테스트를 거친 후 사용하시기 바랍니다.

<details><summary><b>:kr: 한국어</b></summary>

## 💬 플라즈마란?
* **Plazma**는 [Andromeda](https://github.com/EarendelArchived/Andromeda)와 [Fusion](https://github.com/RuinedTechnologyUnify/Fusion)에서 장점만을 가져온 [Paper](https://github.com/PaperMC/Paper) 기반의 서버 플랫폼 입니다.
* 항상 높은 안정성과 강력한 성능, 빠른 업데이트, 방대한 기능을 제공하기 위하여 노력하고 있습니다.

## 📋 Plazma의 목표

* 빠른 업데이트, 높은 안정성을 가진 서버 플랫폼이 되기 위해 노력하고 있습니다.
* 모드 플랫폼 못지 않은 방대한 기능과 강력한 성능을 제공하기 위해 노력하고 있습니다.
* 바닐라의 패치도 사용자화 할 수 있는 자유로운 플랫폼을 만들기 위해 노력하고 있습니다.

## ⚙️ 주요 기능
1. **강력한 플러그인 생태계**\
   [Paper](https://github.com/PaperMC/Paper)를 기반으로 하고 있어, 인터넷에서 다운로드 가능한 대부분의 최신 플러그인인이 정상 작동합니다.
2. **설정이 필요 없는 최적화**\
   [Pufferfish](https://github.com/pufferfish-gg/Pufferfish)의 모든 패치가 포함되어 있으며, 일부 자체 최적화와 모드가 내장되어 있어 최고의 성능을 제공합니다.
3. **원하는 대로 사용자화하는 게임**\
   Plazma에 포함된 [Purpur](https://github.com/PurpurMC/Purpur)는 게임의 전반적인 속성을 수정할 수 있게 해줍니다.
4. **안전하게 플레이하는 서버**\
   [No Chat Reports](https://github.com/Aizistral-Studios/No-Chat-Reports)가 포함되어 있어 1.19부터 추가된 Mojang의 채팅 신고 시스템을 비활성화 할 수 있으며, 진단 정보 수집기가 완전 제거되어 추적 없는 안전한 서버를 플레이 할 수 있습니다.
5. **가장 빠른 업데이트**\
   [AlwaysUpToDate](https://github.com/PlazmaMC/AlwaysUpToDate)는 Plazma의 포함 패치가 항상 최신으로 유지될 수 있도록 해서, Paper 기반 서버 플랫폼 중에서 가장 빠른 업데이트를 제공하고 있습니다.
6. **기본 구성 파일 최적화**\
   기본 적용되는 구성 파일이 최적화되어 있어, 직접 구성 파일을 최적화 하지 않아도 됩니다.
7. **체계적으로 작동하는 멀티스레드**\
   게임의 메커니즘과 관계 없는 시스템 메커니즘을 비동기화 하여, 지연 시간을 줄여 서버를 최적화 합니다.
8. **불필요한 공간의 사용 차단**\
   비슷한 값을 가진 데이터를 모두 하나로 합쳐 메모리 사용량을 줄입니다.
- **[Plazma에 대해 더 알아보고 싶다면?](https://docs.plazmamc.org/v/ko/plazma/about/patches-list)**

## ⚖️ License
- 패치 파일 상단에 명시되지 않은 한 **본 프로젝트 및 모든 패치는 [MIT 라이선스](LICENSE.md)에 따라 허가됩니다.**
</details>

## 💬 About Plazma...

- **Plazma** is a server platform based on [Paper](https://github.com/PaperMC/Paper) that takes only the advantages of [Andromeda](https://github.com/EarendelArchived/Andromeda) and [Fusion](https://github.com/RuinedTechnologyUnify/Fusion).
- We are always striving to provide high stability, powerful performance, fast updates, and extensive features.

## 📋 Goals of Plazma

- We are working hard to become a server platform with fast updates and high stability.
- We are striving to provide extensive features and powerful performance comparable to mod platforms.
- We are working towards creating a free platform that allows customization of vanilla patches.

## ⚙️ Key Features

1. **Powerful Plugin Ecosystem**\
   Based on [Paper](https://github.com/PaperMC/Paper), most of the latest plugins available on the internet work seamlessly.
2. **Optimization without the need for settings**\
   Includes all patches from [Pufferfish](https://github.com/pufferfish-gg/Pufferfish), with some internal optimizations and modes for optimal performance.
3. **Customizable gameplay**\
   [Purpur](https://github.com/PurpurMC/Purpur) included in Plazma allows modification of various game attributes.
4. **Secure server gameplay**\
   Includes [No Chat Reports](https://github.com/Aizistral-Studios/No-Chat-Reports) to disable Mojang's chat reporting feature introduced in 1.19, and completely removes diagnostic information collectors for a traceless, secure server gameplay.
5. **Fastest updates**\
   [AlwaysUpToDate](https://github.com/PlazmaMC/AlwaysUpToDate) ensures that Plazma's included patches are always kept up to date, providing the fastest updates among Paper-based server platforms.
6. **Optimized default configuration files**\
   The default configuration files are optimized, eliminating the need for manual optimization.
7. **Systematically functioning multithreading**\
   Asynchronously synchronizing system mechanisms unrelated to game mechanics to reduce latencry and optimize the server.
8. **Blocking unnecessary space usage**\
   Consolidating data with similar values reduces memory usage.
- **[Want to learn more about Plazma?](https://docs.plazmamc.org/plazma/about/patches-list)**

## ⚖️ License
- This project and all patches are licensed under the [MIT license](LICENSE.md) unless otherwise noted in the patch headers.

## 🌀 Sponsorship - Minecraft Development Dictionary (KOREAN)
[![Minecraft Development Dictionary](https://img.shields.io/discord/911980670123905054?color=%239c91fd&label=MDD%20%28CLICK%20TO%20JOIN%29&logo=discord&style=for-the-badge&logoColor=ffffff)](https://discord.gg/AZwXTA9Pgx)
- 한글로 번역&정리된 Minecraft와 서드파티 버킷들의 소식들을 빠르게 만나볼 수 있습니다.
- Skript와 Plugin등 서버 개발과 관련된 질문에 대한 답변을 받으실 수 있습니다.

## 📈 bStats
[![bStats](https://api.plazmamc.org/internal/cdn/bstats)](https://bstats.org/plugin/server-implementation/Plazma)
