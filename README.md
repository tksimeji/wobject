# Wobject

A Minecraft library that links objects in the world with Java objects

![Version](https://img.shields.io/badge/version-0.2.0_dev-blue?style=flat-square)
![Licence](https://img.shields.io/badge/licence-MIT-red?style=flat-square)

## Get Started

Add Wobject to your plugin's dependencies.

Wobject is available on Maven Central.

### Gradle (Groovy DSL)
```groovy
dependencies {
    compileOnly 'com.tksimeji:wobject:x.y.z'
}
```

### Gradle (Kotlin DSL)
```kotlin
dependencies {
    compileOnly("com.tksimeji:wobject:x.y.z")
}
```

### Maven

```xml
<dependency>
    <groupId>com.tksimeji</groupId>
    <artifactId>wobject</artifactId>
    <version>x.y.z</version>
    <scope>provided</scope>
</dependency>
```

Next, specify the plugin dependencies.
Write the following in `plugin.yml`.

```yaml
depend:
  - Wobject
```

However, the case of `paper-plugin.yml` seems to
be [slightly different](https://docs.papermc.io/paper/dev/getting-started/paper-plugins).

For a plugin that uses Wobject to work, Wobject must be installed as a plugin on the server along with the plugin.

Installing it on the server is no different from a normal plugin.
Just download the jar file from "Releases" and place it in the plugins directory on your server.

## Commands

All commands are provided as subcommands of `/wobject`.

```
/wobject <subcommand> [args...]
```

### /wobject class-list

Lists the wobject classes known to Wobject.

permission: `wobject.class-list`

### /wobject new <class\>

Create a new wobject.

permission: `wobject.new`

### /wobject wobject-list

Lists the wobjects present on the server.

permission: `wobject.wobject-list`


## Usage

### 1. Define the Wobject class.

Define a class with the `com.tksimeji.wobject.api.Wobject` annotation.

```java
@Wobject("namespace:key")
public class MyWobject {
}
```

### 2. Declare the component.

Components are the building blocks of objects. They can be one or more types.

Block or entity is supported.

```java
// Note: The field name will be the comopnent name.

@com.tksimeji.wobject.api.BlockComponent({Material.BLOCK_1, Material.BLOCK_2})
private Block blockComponent;

@com.tksimeji.wobject.api.EntityComponent({EntityType.ENTITY_1, EntityType.ENTITY_2})
private Entity entityComponent;
```

### 3. Declare the handler.

The handler is special method that is called when a specific event occurs.
It is declared with the `com.tksimeji.wobject.event.Handler` annotation.

```java
@Handler
public void onBlockBreak(@NotNull com.tksimeji.wobject.event.BlockBreakEvent event) {
    // Called when a block component is destroyed
    // Important: If you do not cancel the event, the wobject will be killed
}

@Handler
public void onBlockInteracted(@NotNull com.tksimeji.wobject.event.BlockInteractedEvent event) {
    // Called when a block component is interacted with
}

@Handler
public void onBlockRedstone(@NotNull com.tksimeji.wobject.event.BlockRedstoneEvent event) {
    // Called when the regstone signal supplied to a block component changes
}

@Handler
public void onEntityDamage(@NotNull com.tksimeji.wobject.event.EntityDamageEvent event) {
    // Called when an entity component takes damage
}

@Handler
public void onEntityInteracted(@NotNull com.tksimeji.wobject.event.EntityInteractedEvent event) {
    // Called when an entity component is interacted with
}

@Handler
public void onEntityMove(@NotNull com.tksimeji.wobject.event.EntityMoveEvent event) {
    // Called when an entity component moves
}

@Handler
public void onKill(@NotNull KillEvent event) {
    // Called when the wobject is killed for some reason
}

@Handler
public void onTick(@NotNull TickEvent event) {
    // Called every server tick
}
```

Annotations can be used to restrict or prioritize components.

```java
@Handler(component = {"component1", "component2", "..."}, priority = 1)
public void onEvent(@NotNull Event event) {
}
```

### 4. Register the Wobject class

Classes must be registered before the server is full started, 
after which no changes will be accepted.

```java
@Override
public void onEnable() {
    Wobject.register(MyWobject.class);
}
```
