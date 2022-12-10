# NovoLux [![License](https://img.shields.io/badge/license-Apache--2.0-green)](https://github.com/AK1HIKO/NovoLux/blob/main/LICENSE)

NovoLux is a simple, pure-Java 3D Game Engine.
Implements an optimized Entity-Component-System, and renders
everything using the standard Swing and AWT GUI libraries.

## Getting Started
### Project Structure
Project consists of 3 subprojects:
- "ecs" - Entity Component System, may be used independently of the engine itself.
- "engine" - NovoLux Engine - complete 3D engine, shipping with lightweight InputSystem.
- "demo" - Demo Showcase of the engine's capabilities.
### Building
In order to build a subproject, run:
```shell
./gradlew name:build
```
Or
```shell
./gradlew build
```
To build all subprojects.
## Features
- ECS is lazy-initialized, which minimizes the RAM usage.
- ECS caches everything, which significantly improves subsequent fetch-times.
- Full Hardware-agnostic 3D renderer.
- Support for basic shading.
- Supports smooth-shading.
- Supports perspective-based texture mapping.
- Supports most modern 3D rendering optimizations (early z-rejection, view frustum culling, backface culling, etc.).

## Project State
The project itself is not maintained. The main objective of it was to develop a Proof-of-Concept 3D engine with fast ECS for
the university assignment.

## Acknowledgements
The design and overall implementations of the project were heavily
inspired by these excellent projects and videos:
- [@TheCherno](https://www.youtube.com/channel/UCQ-W1KE9EYfdxhL6S4twUNw) and his game engine [Hazel](https://github.com/TheCherno/Hazel) 
as an example of the overall structure and architecture of a game engine.
- [flecs](https://github.com/SanderMertens/flecs) - blazingly fast and lightweight Entity-Component-System, that greatly helped during the
optimization of my implementation of ECS.
- [@javidx9](https://www.youtube.com/@javidx9) and his videos that introduced me to the 3D rasterization and helped in the 
implementation of the foundation for the custom 3D renderer.
- [Unity DOTS](https://unity.com/dots) overall inspiration, and a lot of class descriptions were taken from their talks and
documentation.
- [NS's Doom Texture Expansion Pack](https://forum.zdoom.org/viewtopic.php?f=37&t=62118) good quality texture atlas, used in
demo scene.
- [Scratchapixel](https://www.scratchapixel.com/) amazing resource, that explains a lot of computer graphics mathematics.
- [Computer Graphics from Scratch: A Programmer's Introduction to 3D Rendering](https://www.amazon.com/Computer-Graphics-Scratch-Gabriel-Gambetta/dp/1718500769)
major inspiration for the overall architecture and basic algorithms for 3D rasterization.
- [Tiny Renderer](https://github.com/ssloy/tinyrenderer) a lot of optimizations were taken from this course.
