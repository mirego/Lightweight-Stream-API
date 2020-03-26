@GrabResolver('https://s3.amazonaws.com/mirego-maven/public')
@GrabExclude('org.codehaus.groovy:groovy-all')
@Grab('com.mirego.jenkins:jenkins-jobs:1.2')
import com.mirego.jenkins.jobs.Context

Context context = Context.load(this)
context.standardFolders()

job(context.jobFullName) {
    description("CI check of $projectName")
    logRotator(5)
    scm {
        git {
            branch("${GIT_BRANCH}")
            remote {
                name('origin')
                url("${GIT_URL}")
                credentials('github')
            }
            extensions {
                userExclusion {
                    excludedUsers(['jenkins', 'Jenkins', 'jenkins@mirego.com', 'mirego-builds'].join('\n'))
                }
            }
        }
    }
    triggers {
        scm('H/15 * * * *')
    }
    wrappers {
        timeout {
            absolute(5)
        }
    }
    steps {
        gradle {
            useWrapper()
            makeExecutable()
            tasks('clean check runCheckTasks')
            switches('-i -s')
        }
    }
    publishers {
        slackNotifier {
            notifyFailure(true)
            notifyBackToNormal(true)
            room(context.slackChannel)
        }
    }
}
