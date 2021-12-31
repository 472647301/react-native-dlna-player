# react-native-dlna-player.podspec

require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-dlna-player"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.description  = <<-DESC
                  react-native-dlna-player
                   DESC
  s.homepage     = "https://github.com/472647301/react-native-dlna-player"
  # brief license entry:
  s.license      = "MIT"
  # optional - use expanded license entry instead:
  # s.license    = { :type => "MIT", :file => "LICENSE" }
  s.authors      = { "Byron" => "byron.zhuwenbo@gmail.com" }
  s.platforms    = { :ios => "9.0" }
  s.source       = { :git => "https://github.com/472647301/react-native-dlna-player.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,c,cc,cpp,m,mm,swift}"
  s.requires_arc = true
  s.static_framework = true

  s.dependency "React"
  
  s.vendored_frameworks = 'Neptune.framework', 'Platinum.framework', 'IJKMediaFrameworkWithSSL.framework'

  s.frameworks  = "AudioToolbox", "AVFoundation", "CoreGraphics", "CoreMedia", "CoreVideo", "MobileCoreServices", "OpenGLES", "QuartzCore", "VideoToolbox", "Foundation", "UIKit", "MediaPlayer"
  s.libraries   = "bz2", "z", "stdc++"
end

