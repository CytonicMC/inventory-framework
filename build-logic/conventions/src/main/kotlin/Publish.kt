import org.gradle.api.Project
import org.gradle.authentication.http.BasicAuthentication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

internal fun Project.configureInventoryFrameworkPublication() {
    plugins.apply("maven-publish")

    extensions.configure<PublishingExtension> {
        repositories {
            maven {
                name = "FoxikleCytonicRepository"
                url = uri("https://repo.foxikle.dev/cytonic")
                // Use providers to get the properties or fallback to environment variables
                var u = System.getenv("REPO_USERNAME")
                var p = System.getenv("REPO_PASSWORD")

                if (u == null || u.isEmpty()) {
                    u = "no-value-provided"
                }
                if (p == null || p.isEmpty()) {
                    p = "no-value-provided"
                }

                val user = providers.gradleProperty("FoxikleCytonicRepositoryUsername").orElse(u).get()
                val pass = providers.gradleProperty("FoxikleCytonicRepositoryPassword").orElse(p).get()
                credentials {
                    username = user
                    password = pass
                }
                authentication {
                    create<BasicAuthentication>("basic") {

                    }
                }
            }
        }
        publications {
            create<MavenPublication>("maven") {
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
                from(components.getByName("java"))
            }
        }
    }
}
