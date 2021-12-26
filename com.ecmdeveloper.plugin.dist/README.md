This folder contains the distribution files for the eclipse plugin. Use the following commands to deploy the update site.

Goto the `com.ecmdeveloper.tycho` folder and build the plugin:

```
mvn install
```

Goto the `com.ecmdeveloper.plugin.repository` folder and copy the distribution files to the `com.ecmdeveloper.plugin.dist` folder:

```
cp -R target/repository/* ../com.ecmdeveloper.plugin.dist
cp target/com.ecmdeveloper.plugin.repository-2.3.0.zip ../com.ecmdeveloper.plugin.dist
```

Goto to the root folder, commit the distribution files and push to the `gh-pages` branch:

```
git subtree push --prefix com.ecmdeveloper.plugin.dist origin gh-pages
```