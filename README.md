[<div align="center">

[![image](https://raw.githubusercontent.com/PlazmaMC/Plazma/main/src/resources/title-900.png)](https://github.com/PlazmaMC/Plazma)

### A Server Platform for Minecraft: Java Edition based on [Paper](https://github.com/PaperMC/Paper)

[![Discord](https://img.shields.io/discord/1083716853928558652?style=for-the-badge&logo=discord&logoColor=ffffff&label=DISCORD&color=5865F2&link=https%3A%2F%2Fdiscord.gg%2FMmfC52K8A8)](https://discord.gg/MmfC52K8A8)
[![Build Status](https://img.shields.io/github/actions/workflow/status/PlazmaMC/Plazma/build.yml?branch=ver/1.20.2&logo=GoogleAnalytics&style=for-the-badge&logoColor=ffffff)](https://github.com/PlazmaMC/Plazma/actions/workflows/build.yml?query=branch:ver/1.20.2)
[![MC Version](https://img.shields.io/badge/MC-1.20.2-6047ff?&logo=Webpack&style=for-the-badge&logoColor=ffffff)](https://github.com/PlazmaMC/Plazma/releases/build/1.20.2/latest)

[![License](https://img.shields.io/github/license/PlazmaMC/Plazma?logo=github&style=flat-square&logoColor=ffffff)](LICENSE)
[![Downloads](https://img.shields.io/github/downloads/PlazmaMC/Plazma/total?label=Downloads&logo=data:image/png;base64,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&style=flat-square&color=green)](https://github.com/PlazmaMC/Plazma/releases)
[![Stargazers](https://img.shields.io/github/stars/PlazmaMC/Plazma?label=stars&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABhWlDQ1BJQ0MgcHJvZmlsZQAAKJF9kT1Iw0AcxV9bxSIVh1YQcYhQnSyIiuimVShChVArtOpgcukXNGlIUlwcBdeCgx+LVQcXZ10dXAVB8APE0clJ0UVK/F9SaBHjwXE/3t173L0D/PUyU82OMUDVLCOViAuZ7KrQ9YogwujDEGYkZupzopiE5/i6h4+vdzGe5X3uz9Gj5EwG+ATiWaYbFvEG8dSmpXPeJ46woqQQnxOPGnRB4keuyy6/cS447OeZESOdmieOEAuFNpbbmBUNlXiSOKqoGuX7My4rnLc4q+Uqa96TvzCU01aWuU5zEAksYgkiBMioooQyLMRo1UgxkaL9uId/wPGL5JLJVQIjxwIqUCE5fvA/+N2tmZ8Yd5NCcaDzxbY/hoGuXaBRs+3vY9tunACBZ+BKa/krdWD6k/RaS4seAb3bwMV1S5P3gMsdoP9JlwzJkQI0/fk88H5G35QFwrdA95rbW3Mfpw9AmrpK3gAHh8BIgbLXPd4dbO/t3zPN/n4Ax9dyyerighsAAAAGYktHRAAAAAAAAPlDu38AAAAJcEhZcwAADdcAAA3XAUIom3gAAAAHdElNRQfmCBMVNjtc7/hFAAABIElEQVQ4y62SzS5DURSFv6smXkAUCRU0UdKYGNTPyCsYYOYFGGi8Ao9QM0PxCh6CgQ4qfiLpBFEjdKCfySaXtDch1uScs9Ze62TvcyAD6o66zV+gjqpvalsd61XXl5GxBySx3/3t7UPqi1pTD9VXdaRbbZIyDQLTwBSwBqzGGaABnAInwCXQSJLk/tO4orb8jra6nwo/CC6NlrqMOq421Y5aVSfUXJe2cqFVo7b5NdwIuVaf1IWM2cyrD+qdOvlTLERIS53pYi6FdqMWet2wGP1tdNE2Q1vK+gfDsdbDlFfzwV3Ems8KmAXegcd4hSvgVq0Bz6GV0ob+HgF1YAA4Cn4LWA9tLusHnscTHavFFF8MrqOeZQVU1HKGXlYr/Cc+AKuOI2h/Jrf7AAAAAElFTkSuQmCC&style=flat-square&color=green)](https://github.com/PlazmaMC/Plazma/stargazers)
[![Forks](https://img.shields.io/github/forks/PlazmaMC/Plazma?label=forks&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABhWlDQ1BJQ0MgcHJvZmlsZQAAKJF9kT1Iw0AcxV9bxSIVh1YQcYhQnSyIiuimVShChVArtOpgcukXNGlIUlwcBdeCgx+LVQcXZ10dXAVB8APE0clJ0UVK/F9SaBHjwXE/3t173L0D/PUyU82OMUDVLCOViAuZ7KrQ9YogwujDEGYkZupzopiE5/i6h4+vdzGe5X3uz9Gj5EwG+ATiWaYbFvEG8dSmpXPeJ46woqQQnxOPGnRB4keuyy6/cS447OeZESOdmieOEAuFNpbbmBUNlXiSOKqoGuX7My4rnLc4q+Uqa96TvzCU01aWuU5zEAksYgkiBMioooQyLMRo1UgxkaL9uId/wPGL5JLJVQIjxwIqUCE5fvA/+N2tmZ8Yd5NCcaDzxbY/hoGuXaBRs+3vY9tunACBZ+BKa/krdWD6k/RaS4seAb3bwMV1S5P3gMsdoP9JlwzJkQI0/fk88H5G35QFwrdA95rbW3Mfpw9AmrpK3gAHh8BIgbLXPd4dbO/t3zPN/n4Ax9dyyerighsAAAAGYktHRAAAAAAAAPlDu38AAAAJcEhZcwAADdcAAA3XAUIom3gAAAAHdElNRQfmCBMVNCYN3/YeAAAA/UlEQVQ4y7WTQUoDQRBFf01czlJcxUyOINGjjAvFHMFzZGdygOwDwTtk6UZcqLlAxCAuMigug89FamIzdAIN+qGhq/6v6qrqbumvAJwBj8AHMAQs4DJgBHy65jSW4Bl4AaZsUAbcufumrnmquSzIcSzpTtLA7XbA1fuBa9qxCob8YgUUAdcFqoC/iSXIgLELOhG+49w4nM+2BTP7ljR3M4/MufbNzYxdN1E0Sm2ialZnsVIllZKOJF24eyLpXdKtmS1S3sYMmO3THOwJziUdbrbkZvaVcnILeAh6vweylAQ9D7z2BXCS0sJS0lrSpdtrSW+pn6sPLIFX4Er/hR9C0wl1FTBzNwAAAABJRU5ErkJggg==&style=flat-square&color=green)](https://github.com/PPlazmaMC/Plazma/network/members)
[![Watchers](https://img.shields.io/github/watchers/PlazmaMC/Plazma?label=watchers&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABhWlDQ1BJQ0MgcHJvZmlsZQAAKJF9kT1Iw0AcxV9bxSIVh1YQcYhQnSyIiuimVShChVArtOpgcukXNGlIUlwcBdeCgx+LVQcXZ10dXAVB8APE0clJ0UVK/F9SaBHjwXE/3t173L0D/PUyU82OMUDVLCOViAuZ7KrQ9YogwujDEGYkZupzopiE5/i6h4+vdzGe5X3uz9Gj5EwG+ATiWaYbFvEG8dSmpXPeJ46woqQQnxOPGnRB4keuyy6/cS447OeZESOdmieOEAuFNpbbmBUNlXiSOKqoGuX7My4rnLc4q+Uqa96TvzCU01aWuU5zEAksYgkiBMioooQyLMRo1UgxkaL9uId/wPGL5JLJVQIjxwIqUCE5fvA/+N2tmZ8Yd5NCcaDzxbY/hoGuXaBRs+3vY9tunACBZ+BKa/krdWD6k/RaS4seAb3bwMV1S5P3gMsdoP9JlwzJkQI0/fk88H5G35QFwrdA95rbW3Mfpw9AmrpK3gAHh8BIgbLXPd4dbO/t3zPN/n4Ax9dyyerighsAAAAGYktHRAAAAAAAAPlDu38AAAAJcEhZcwAADdcAAA3XAUIom3gAAAAHdElNRQfmCBMVNw4TRw0nAAAA3UlEQVQ4y83SP04CURAG8I0lewHOwAFUaiwkdmAlp8CL4FHopfIvtOIJWE3opIBK489mQPKy6xYWOskkL9/MN/PNzMuyf2fIcYkZVuGzwPI68gle8Yl7jMIfAntBp4o8wAeecFgSP8I8cgZp8DwC12j8oLCBCd7R34ItbHCzT8ZZSC7QTYrcYo1WhjGWaCbdCt+2SGLN4IwPfnu07QjrkhG6oWKB0+TMd7sRAuzHYuqWmO8tsVd1xjmOS8htPEfORVWHTmweHnEVPg2sqPxIicxhFFjhLd7D2q/8J/YFHSJt9VSqQ08AAAAASUVORK5CYII=&style=flat-square&color=green)](https://github.com/PlazmaMC/Plazma/watchers)

</div>

[main]: https://github.com/PlazmaMC/Plazma

> [!IMPORTANT]
This is the branch for Plazma 1.20.2. If you want to know more about Plazma, please check the **[main branch][main]**.<br>
이곳은 플라즈마의 1.20.2용 분기입니다. 플라즈마에 대해 자세히 알고 싶다면 **[main 브랜치][main]** 를 확인해주세요.

## ⬇️ Downloads
> [!NOTE]
If you don't know about Mojmap or Bundler, download **Reobf Paperclip**<br>
Mojmap 또는 Bundler에 대해 잘 알지 못한다면, **Reobf Paperclip**을 사용하세요

<!--- LINKS --->
[paperReobf]: https://github.com/PlazmaMC/Plazma/releases/download/build/1.20.2/latest/plazma-paperclip-1.20.2-R0.1-SNAPSHOT-reobf.jar
[paperMojmap]: https://github.com/PlazmaMC/Plazma/releases/download/build/1.20.2/latest/plazma-paperclip-1.20.2-R0.1-SNAPSHOT-mojmap.jar
[bundlerReobf]: https://github.com/PlazmaMC/Plazma/releases/download/build/1.20.2/latest/plazma-bundler-1.20.2-R0.1-SNAPSHOT-reobf.jar
[bundlerMojmap]: https://github.com/PlazmaMC/Plazma/releases/download/build/1.20.2/latest/plazma-bundler-1.20.2-R0.1-SNAPSHOT-mojmap.jar

| **Reobf Paperclip (Default)** |    Mojmap Paperclip     |      Reobf Bundler       |      Mojmap Bundler       |
|:-----------------------------:|:-----------------------:|:------------------------:|:-------------------------:|
|    [Download][paperReobf]     | [Download][paperMojmap] | [Download][bundlerReobf] | [Download][bundlerMojmap] |
