# ODSOFT_2425_1182147_1190409

Repository for ODSOFT's Semester Project

## Table of Contents
- [Local Kickstart](#local-kickstart)
  - [API-Ninja](#api-ninja)
- [Jenkins Pipeline](#jenkins-pipeline)
  - [Tools and Plugins](#tools-and-plugins)
  - [Source Code Management (SCM)](#source-code-management-scm)
  - [Compilation](#compilation)
  - [Static Code Analysis](#static-code-analysis)
  - [Unit + Mutation Testing w/ Coverage Reports](#unit--mutation-testing-with-coverage-reports)
    - [Unit Testing](#unit-testing)
    - [Unit Testing Report Coverage](#unit-testing-report-coverage)
    - [Mutation Testing](#mutation-testing)
    - [Mutation Testing Report Coverage](#mutation-testing-report-coverage)
    - [Optimizations](#optimizations)
  - [Record Static Analysis Report](#record-static-analysis-report)
  - [Package](#package)
  - [Archive JAR Artifact](#archive-jar-artifact)
  - [Deploy](#deploy)

## Local Kickstart

To run the application locally:

```shell
mvn spring-boot:run
```

- Go to the following URL: https://vs-gate.dei.isep.ipp.pt:31349/swagger-ui
- Go to the bottom of the page to the Authentication API.
- You can log in as one of the following Users:

| User             | Password          | Role              |
|------------------|-------------------|-------------------|
| maria@gmail.com  | Mariaroberta!123  | Librarian (Admin) |
| manuel@gmail.com | Manuelino123!     | Reader            |
| joao@gmail.com   | Joaoratao!123     | Reader            |
| pedro@gmail.com  | Pedrodascenas!123 | Reader            |
| ...other Readers | ...               | Reader            |

- Once you log in with a User, extract the Auth Token from the Response.
- Go to the top-right of the page to the green button with a padlock saying 'Authorize'
- Paste the Authorization Token there.
- Now you can use the APIs freely (if the Role allows).

### API-Ninja

It doesn't expose many endpoints. Only important part is that you need a custom `X-API-KEY` HTTP Request Header. Therefore:

```shell
curl 'https://api.api-ninjas.com/v1/jokes' -H 'X-API-KEY: <key>';

curl 'https://api.api-ninjas.com/v1/historicalEvents?year=2015&month=12' -H 'X-API-KEY: <key>'
```

## Jenkins Pipeline

### Integration with GitHub

In order to trigger Jenkins Pipeline builds from Commits, we have installed a Webhook on our GitHub repository with the
following configurations:

![Github Webhook Configuration](/Docs/GitHubWebhook/github-webhook-config.png)

It targets specifically the `/github-webhook/` Payload URL Path which is critical to trigger the GitHub Plugin API in Jenkins.
Furthermore, we've disabled SSL Verification per the professor recommendation due to DEI VM constraints and, for now, have chosen
solely to have the pipeline triggered on `push` events.

### Tools and Plugins

Apart from the Recommended Tools installed in Jenkins by default, we have chosen to utilise the following:

- **Blue Ocean**: Used for Pipeline Stage visualization and management. Used extensively for visualizing performance improvements and pipeline flow;
- **JaCoCo Plugin**: Used for visualizing Code Coverage reports and trends;
- **JUnit Plugin**: Used for visualizing Test Results reports and trends;
- **Maven Integration**: Used for abstracting Maven binary paths to keep the Job environment agnostic of Operating System in that regard;
- **Pipeline: Stage View Plugin**: Used for an easy to digest Pipeline Stage visualization and management directly from the Jenkins Dashboard as opposed to Blue Ocean which is a separate Dashboard;
- **Warnings Plugin**: Used as an extensible aggregator of Static Code Analysis Tool reports.

### Source Code Management (SCM)

**Git** is currently the Source Code Management System of choice.

The corresponding stage for retrieving the Source Code within the Jenkins Pipeline is the following:

```groovy
stage('Checkout') {
    steps {
        checkout scm
    }
}
```

This approach was chosen due to the automatic detection of the SCMS (i.e., Git) and Repository plus target Branch
currently configured in the Pipeline specifications. It is more concise than explicitly specifying both the
Repository and Branch within the Groovy script and leverages the inherent flexibility of the plugin if the need
to extend its capabilities ever arises.

### Compilation

```groovy
stage('Build') {
    steps {
        script {
            if(isUnix()) {
                sh "mvn clean compile"
            } else {
                bat "mvn clean compile"
            }
        }
    }
}
```

In this step we first verify the leveraging of the native `ìsUnix()` pipeline function which, as the name implies,
verifies whether the Jenkins Agent is running on a Unix system (i.e., Linux or Mac) or not (i.e., Windows). This
then mostly has the ramification of either using `sh` (**Shell**) to run the script on Unix systems or `bat`
(**Batch**) to run the script on Windows systems.

The script itself simply leverages **Maven**'s `clean` and `compile` lifecycle phases as an initial post-checkout step.
This guarantees a clean workspace and asserts that the project, at the very least, compiles before proceeding
with the rest of the pipeline and that it has the necessary dependencies installed.

### Static Code Analysis

```groovy
stage('Static Code Analysis') {
    steps {
        script {
            if(isUnix()) {
                sh "mvn checkstyle:checkstyle"
            } else {
                bat "mvn checkstyle:checkstyle"
            }
        }
    }
}
```

This step simply runs the [Checkstyle](https://checkstyle.sourceforge.io) Maven Reporting Plugin.

Checkstyle is well-known and widely used static code analysis tool for checking if Java source code complies
with specified coding rules and standards, hence it was a natural choice for this pipeline step.
In the future we might add other tools such as SonarLint and Spotbugs.

In our case we're using Sun's Checkstyle Configuration -- https://github.com/checkstyle/checkstyle/blob/master/src/main/resources/sun_checks.xml

### Unit + Mutation Testing with Coverage Reports

This section will be split into both types of testing as they're parallelized in the pipeline. First we'll
address Unit Tests, then Mutation Tests and the optimizations done to improve their computation velocity, ending with the
statement on why parallelizing both stages improved the pipeline performance as a whole and why we have made that choice.

#### Unit Testing

```groovy
stage('Run Unit Tests') {
    steps {
        script {
            if(isUnix()) {
                sh "mvn test"
            } else {
                bat "mvn test"
            }
        }
    }
}
```

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.5.1</version>
    <configuration>
        <includes>**/*Test.java</includes>
    </configuration>
</plugin>
```

Running the Unit Tests was performed by leveraging Maven's `test` lifecycle phase and by constricting the tests
being run to ones which end in `Test.java` by configuring `maven-surefire-plugin`. This was done as advised by
the professor in Theoretical classes as then it allows us to explicitly ignore Integration Tests in this pipeline stage.

#### Unit Testing Report Coverage

```groovy
stage('Record Unit Test Reports') {
    steps {
        script {
            def reportName = "Unit Test Coverage Report - Build #${env.BUILD_NUMBER}"
            // This gives visibility within Jenkins as to the state of the Coverage Reports
            // and allows analyzing Coverage Trends
            jacoco(
                    execPattern: 'target/*.exec',
                    classPattern: 'target/classes',
                    sourcePattern: 'src/main/java',
                    exclusionPattern: 'src/test*'
            )
            publishHTML(target: [
                    allowMissing: false,
                    alwaysLinkToLastBuild: false,
                    keepAll: true,
                    reportDir: 'target/site/jacoco',
                    reportFiles: 'index.html',
                    reportName: reportName
            ])
            junit 'target/surefire-reports/*.xml'
        }
    }
}
```

The recording of the Unit Tests Report Coverage was done by leveraging [JaCoCo](https://eclemma.org/jacoco) Jenkins plugin.
Even though, as seen on the code above, we are explicitly defining the plugin variables, they're all default. However,
we have decided to keep it this way for the sake of clarity of pattern targeting for debugging purposes. Nevertheless,
the variables define the following:
- `execPattern`: where to find the execution data files;
- `classPattern`: where the compiled classes are located;
- `sourcePattern`: where the source code is located;
- `exclusionPattern`: what to exclude from the coverage report

In order to generate the artifact containing the coverage report to be persisted by Jenkins, we used the native
`publishHTML()` pipeline function. This function has the role of retrieving the coverage report generated by JaCoCo and
publishing its artifact. In order to enhance the reliability of the reports in case of pipeline failure and verifying
coverage trends, the variables imposed to the function are the following:
- `allowMissing: false`: it disallows missing reports entirely (e.g., failed to be generated or wrong relative path);
- `alwaysLinkToLastBuild: false`: this guarantees that each report links to the build that generated it, as opposed to, as the name implies, always linking to the last build;
- `keepAll: true`: ensures that all artifacts are kept. This is critical for coverage trends;
- `reportDir: 'target/site/jacoco'`: points the function to the given directory for the location of the report files;
- `reportFiles: 'index.html'`: the files to be published;
- `reportName: reportName`: the name to be given to the report. This is declared above programmatically with the build number.

Finally, in order to persist the **Test Results** report, we leverage the `junit` Jenkins Plugin, which provides us
with a concise way of collecting and processing JUnit-style XML test reports and then artifact them, which much like JaCoCo's
step above, provides us with a way of tracking test result trends.

#### Mutation Testing

```groovy
stage('Run Mutation Tests') {
    steps {
        script {
            if(isUnix()) {
                sh "mvn test-compile org.pitest:pitest-maven:mutationCoverage"
            } else {
                bat "mvn test-compile org.pitest:pitest-maven:mutationCoverage"
            }
        }
    }
}
```

```xml
<plugin>
    <groupId>org.pitest</groupId>
    <artifactId>pitest-maven</artifactId>
    <version>1.17.0</version>
    <configuration>
        <targetClasses>
            <param>**.model.**</param>
        </targetClasses>
        <targetTests>
            <param>**.model.**</param>
        </targetTests>
        <!-- Optimizations based on: https://blog.frankel.ch/faster-mutation-testing -->
        <threads>4</threads>
    </configuration>
</plugin>
```

Running the Unit Tests was performed by leveraging Maven's `test-compile` lifecycle phase in order to have the Test classes
compiled and dependency resolution. Afterward, we call `org.pitest:pitest-maven:mutationCoverage` to run the Mutation Tests.

As shown in the `pom.xml` excerpt, we're restricting the Mutation Tests to `*.model` classes (i.e., domain classes) as they're
the most critical business-logic wise and allows us to restrict the scope of Mutation Testing which, by itself, improves performance.
Furthermore, as referenced by the blog post, we've implemented multi-threading optimizations to further optimize this step
by utilizing 4 threads as opposed to 1.
We've made some concessions which will be explored in the [optimization section of this document](#optimizations).

#### Mutation Testing Report Coverage

```groovy
stage('Record Mutation Test Reports') {
    steps {
        script {
            def reportName = "Mutation Test Coverage Report - Build #${env.BUILD_NUMBER}"
            publishHTML(target: [
                    allowMissing: false,
                    alwaysLinkToLastBuild: false,
                    keepAll: true,
                    reportDir: 'target/pit-reports',
                    reportFiles: 'index.html',
                    reportName: reportName
            ])
        }
    }
}
```

The way we approached this phase is exactly the same as [Unit Testing Report Coverage](#unit-testing-report-coverage),
as such we'll only approach the part that differs, which is:
- `reportDir: 'target/pit-reports'`: the only difference is the target report directory.

#### Optimizations

Mutation Testing is computationally very intensive and, therefore, a bottleneck for low-resource environments such
as DEI's virtual machines. Unfortunately, measuring performance in DEI's virtual machines is highly unreliable as it
is highly dependent on resource availability which is extremely volatile, so testing times vary wildly from build to build.
As such, we'll showcase the improvements from local builds, as the times are much more stable even if still subject
to deviations.

##### Pre-Optimization Unit Test Stage
![Pre-Optimization Unit Tests](/Docs/PipelineOptimizations/PreOptimizations/pre-optimizations-unit.png)

##### Pre-Optimization Mutation Test Stage
![Pre-Optimization Mutation Tests](/Docs/PipelineOptimizations/PreOptimizations/pre-optimizations-mutation.png)

##### Pre-Optimization Total Time
![Pre-Optimization Total Time](/Docs/PipelineOptimizations/PreOptimizations/pre-optimizations-total.png)

The first optimization we performed was parallelizing both Unit Testing and Mutation Testing in the Jenkins Pipeline:

```groovy
// Both Unit Tests and Mutation Tests can and should be ran in parallel
// as there is no intrinsic benefit in doing them separately.
stage('Unit + Mutation Testing w/ Report Recording') {
   parallel {
       stage('Unit Tests') {
           stages {
               stage('Run Unit Tests') {
                   // ...shown above in its respective section
               }
               stage('Record Unit Test Reports') {
                   // ...shown above in its respective section
               }
           }
       }
       stage('Mutation Tests') {
           stages {
               stage('Run Mutation Tests') {
                   // ...shown above in its respective section
               }
               stage('Record Mutation Test Reports') {
                   // ...shown above in its respective section
               }
           }
       }
   }
}
```

While arguments can be made for the semantic and computational benefit of running Unit Tests and Integration Tests separately,
the same cannot be said for Unit Tests and Mutation Tests. With our current configuration, where we do not enforce Mutant kill
coverage, it might not make much sense to parallelize them. However, Mutation Testing is often accompanied by Mutant kill coverage
expectations which can fail the pipeline when not matched, therefore there is no reason **not to** parallelize them.

The images below do not showcase any improvement due to Mutation Testing taking a bit longer than the previous test. However,
it is a certain assumption that given equal Unit Test and Mutation Test times, the total time would be reduced by the Unit Test
time as the bottleneck of the parallel operation are the Mutation Tests.

##### Parallelization Unit Test Stage
![Parallelization Unit Tests](/Docs/PipelineOptimizations/Parallelization/parallelization-unit.png)

##### Parallelization Mutation Test Stage
![Parallelization Mutation Tests](/Docs/PipelineOptimizations/Parallelization/parallelization-unit.png)

##### Parallelization Total Time
![Parallelization Total Time](/Docs/PipelineOptimizations/Parallelization/parallelization-unit.png)

The second optimization we performed was restricting the scope of the Mutation Tests to solely Domain Classes.
This was done due to the fact that the most extensive and critical unitary testing is done on the Domain Classes.
This is a tradeoff, as ideally we would have mutation testing everywhere. However, the cost of doing so in low-resource
environments is tremendous, with the pipeline frequently failing due to timeouts alone. As such, we're keeping the critical
classes being extensively tested for mutants, and disregard the rest.

```xml
<!-- Pitest Plugin -->
<!-- ... -->
<targetClasses>
    <param>**.model.**</param>
</targetClasses>
<targetTests>
    <param>**.model.**</param>
</targetTests>
<!-- ... -->
```

##### Class-Restriction Mutation Test Stage
![Class-Restriction Mutation Tests](/Docs/PipelineOptimizations/MutationClassRestrictions/mutation-class-restriction-mutation.png)

##### Class-Restriction Total Time
![Class-Restriction Total Time](/Docs/PipelineOptimizations/MutationClassRestrictions/mutation-class-restriction-total.png)

The third and final optimization we performed, was enabling multi-threading in the Pitest plugin. This was done by adding the
following property in `pom.xml` Pitest Plugin configuration:

```xml
<!-- Pitest Plugin -->
<!-- ... -->
<!-- Optimizations based on: https://blog.frankel.ch/faster-mutation-testing -->
<threads>4</threads>
<!-- ... -->
```

As noted in the snippet above, this change was inspired by the Blog Post.

We could have gone further by restricting the maximum Mutations per Class to reduce the computation load:

```xml
<!-- Pitest Plugin -->
<!-- ... -->
<features>+CLASSLIMIT(limit[X])</features>
<!-- ... -->
```

However, we decided against this as we already took a big tradeoff by solely applying Mutations to Domain Classes. Hence
the processing time win we'd gain by implementing a `CLASSLIMIT` to Pitest would not only be negligible at this point, but it'd
undermine the testing reliability of the Domain classes.

##### Multi-Threading Mutation Test Stage
![Multi-Threading Mutation Tests](/Docs/PipelineOptimizations/MutationThreading/mutation-threading-mutation.png)

##### Multi-Threading Total Time
![Multi-Threading Total Time](/Docs/PipelineOptimizations/MutationThreading/mutation-threading-total.png)

##### Optimization Conclusions

| Optimization             | Unit Tests Time (in Seconds) | Mutation Tests Time (in Seconds) | Total Pipeline Time (in Seconds) | Improvement (%) |
|--------------------------|------------------------------|----------------------------------|----------------------------------|-----------------|
| Base                     | 17                           | 273                              | 309                              | N/A             |
| Parallel                 | 26                           | 300                              | 318                              | -2.9%*          |
| Pitest Class Restriction | N/A                          | 46                               | 65                               | 79.56%          |
| Pitest Multi-threading   | N/A                          | 26                               | 47                               | 27.69%          |

*Note: Within expected deviation as the main bottleneck are the Mutation Tests. So if those vary wildly, the performance
improvement won't be noticeable.

As we can see in the table above, the secret to less processing time is to do less.

Jokes aside, while parallelizing Unit Tests and Mutation Tests didn't bring noticeable performance gains as the main bottleneck
(i.e., Mutation Tests) is subject to high fluctuations, restricting the Mutation Tests to critical Domain Classes and allowing
Pitest to make use of multiple Threads brought noticeable gains. [Local] Pipelines went from 5 minutes and 9 seconds
to just 47 seconds. **Therefore, amounting to a 84.8% speed improvement in total.**

### Record Static Analysis Report

```groovy
stage('Record Static Analysis Report') {
    steps {
        script {
            recordIssues(
                    enabledForFailure: false,
                    aggregatingResults: false,
                    // This array will contain the tools we utilise for Static Analysis (e.g., Java Compiler, Checkstyle, ...)
                    // For more information regarding the tools natively available to the plugin: https://www.jenkins.io/doc/pipeline/steps/warnings-ng/
                    tools: [checkStyle()]
            )
        }
    }
}
```

In order to record static analysis reports, we're making use of the [Warnings](https://jenkins.io/doc/pipeline/steps/warnings-ng)
plugin. We've chosen this plugin due to its capabilities of aggregating multiple different reports from different static
analysis tools.

The configuration chosen for the `recordIssues` function, which as the name implies, records compiler warnings and
static analysis results can be explained by the following:
- `enabledForFailure: false`: this prevents the static analysis results from being recorded on failed builds. This is relevant since, otherwise, failed builds could realistically pollute trends unnecessarily;
- `aggregatingResults: false`: `false` is already the default value since this parameter is optional. However, for clarity’s sake, it determines that the results of each static analysis tool will have its own separate Dashboard for visualization as opposed to all of them being aggregated in a single Dashboard.

### Package

```groovy
stage('Package') {
    steps {
        script {
            if(isUnix()) {
                sh "mvn install -DskipTests"
            } else {
                bat "mvn install -DskipTests"
            }
        }
    }
}
```

In order to perform the packaging of the application, we make use of Maven's `install` lifecycle phase. However, as
seen in the snippet, we're passing the flag `-DskipTests`. This is due to how Maven lifecycle phases function, as each
call, unless specifically calling sub-phases (e.g., `mvn compile:compile`) will run all previous expected lifecycle phases.
As such, we want to avoid testing especially, hence the flag.

### Archive JAR Artifact

```groovy
// For rollback purposes it is always valuable to keep the artifact of
// the built binary.
stage('Archive JAR Artifact') {
    steps {
        archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: false
    }
}
```

In order to archive the generated JAR artifact for the given build, we make use of Jenkins' native `archiveArtifacts` function.
The parameters are as follows:
- `artifacts: 'target/*.jar'`: the artifacts to be archived. In this case, any `*.jar` file contained in the directory `target`;
- `allowEmptyArchive: false`: this is the default behavior, but we have decided to keep it for clarity. In specifies that we **do not** allow the archiving process to return nothing (i.e., archiving nothing).

### Deploy

```groovy
stage('Deploy') {
    steps {
        script {
            if(isUnix()) {
                sh "JENKINS_NODE_COOKIE=dontKillMe java -jar ./target/psoft-g1-0.0.1-SNAPSHOT.jar --server.port=2228 > output.log 2>&1 &"
            } else {
                bat "set JENKINS_NODE_COOKIE=dontKillMe && start java -jar .\\target\\psoft-g1-0.0.1-SNAPSHOT.jar --server.port=2228 > output.log 2>&1"
            }
        }
    }
}
```

In order to perform the deployment, we've configured a simple script that performs the following steps:

#### Unix

- `JENKINS_NODE_COOKIE=dontKillMe`: an environment variable to prevent background processes spawned by a Jenkins job from being terminated when the job completes;
- `java -jar ./target/psoft-g1-0.0.1-SNAPSHOT.jar`: executes the Java application
- `--server.port=2228`: specifies the server port, which in this case is the one that maps to HTTPS openToInternet in DEI Virtual Machine;
- `> output.log 2>&1`: redirects both stdout and stderr to `output.log` for debugging purposes;
- `&`: runs the process in the background.

#### Windows

- `set JENKINS_NODE_COOKIE=dontKillMe`: an environment variable to prevent background processes spawned by a Jenkins job from being terminated when the job completes;
- `start java -jar .\\target\\psoft-g1-0.0.1-SNAPSHOT.jar`: executes the Java application in the background;
- `--server.port=2228`: specifies the server port, which in this case is the one that maps to HTTPS openToInternet in DEI Virtual Machine;
- `> output.log 2>&1`: redirects both stdout and stderr to `output.log` for debugging purposes; 

