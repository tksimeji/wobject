# Wobject

A Minecraft library that links objects in the world with Java objects

![Version](https://img.shields.io/badge/version-0.0.0-blue?style=flat-square)
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

## Usage

### 1. Define the Wobject class.

Define a class with the `com.tksimeji.wobject.api.Wobject` annotation.

```java
@Wobject("namespace:key")
public class MyWobject {
}
```

### 2. Declare the component.

Components are the building blocks of an object. They can be one or more types.

These are declared as fields of type `org.bukkit.block.Block` with the `com.tksimeji.wobject.api.Component` annotation 
and are automatically injected when an instance is created.

```java
@Component({Material.TORCH, Material.SOUL_TORCH, Material.REDSTONE_TORCH})
private Block torch;
```

### 3. Declare the handler.

The handler is special method that is called when a specific event occurs.
It is declared with the `com.tksimeji.wobject.api.Handler.*` annotation.

```java
// The values that can be obtained from the arguments vary depending on the type of handler.

@Handler.Interact(component = "namespace:component_name")
public void handler1(BlockBreakEvent event, Player player, Block block) {
    // Called when the component is interacted with.
}

@Handler.Redstone(component = "namespace:component_name")
public void handler2(BlockRedstoneEvent event, Block block) {
    // Called when the component's redstone signal strength changes.
}

@Handler.Kill()
public void handler3() {
    // Called when a component is destroyed and an object is killed.
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
