Pod::Spec.new do |s|
  s.name     = 'lightweight-stream-api@J2ObjC-2.7'
  s.version  = '1.2.21'
  s.license  = { :type => 'PRIVATE', :text => 'PRIVATE' }
  s.summary  = 'Stream api port to Java 7.'
  s.homepage = 'http://www.mirego.com'
  s.authors  = { 'Mirego, Inc.' => 'info@mirego.com' }
  s.source   = { :git => 'git@github.com:mirego/Lightweight-Stream-API.git', :tag => 'lightweight-stream-' + s.version.to_s }
  s.requires_arc = false

  s.prepare_command = <<-CMD
    export ANDROID_HOME="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
    export J2OBJC_VERSION="${J2OBJC_VERSION:-2.7}"
    ./gradlew j2objc_pod
  CMD

  s.ios.deployment_target = '8.0'
  s.osx.deployment_target = '10.6'
  s.watchos.deployment_target = '2.0'
  s.tvos.deployment_target = '9.0'

  s.source_files = 'stream-j2objc/**/*.{h,m}'
  s.header_mappings_dir = 'stream-j2objc'

  s.dependency 'J2ObjC@mirego', '>= 2.7'
  s.dependency 'J2ObjC@mirego/jsr305'
end
