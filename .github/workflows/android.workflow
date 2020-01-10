workflow "Build, Test and Distribute" {
  on = "push"
  resolves = "Publish Code Coverage"
}

action "Build" {
  uses = "vgaidarji/android-github-actions/build@v1.0.0"
  secrets = ["FABRIC_API_KEY", "FABRIC_API_SECRET"]
  args = "./gradlew assembleDebug -PpreDexEnable=false"
}
