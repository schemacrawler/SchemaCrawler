schema_version = 1

project {
  license          = "EPL-2.0"
  copyright_holder = "Sualeh Fatehi"
  copyright_year   = 2000

  # (OPTIONAL) A list of globs that should not have copyright/license headers.
  # Supports doublestar glob patterns for more flexibility in defining which
  # files or folders should be ignored
  header_ignore = [
    "**/.git/**",
	"**/.github/**",
	"**/.devcontainer/**",
	"**/.mvn/**",
	"**/.vscode/**",
	"**/.idea/**",
	"**/pom.xml",
	"**/*.css",
    "**/target/**",
	"**/test/resources/**",
	"**/test/**/*.html",
	"**/test/**/*.txt",
	"**/test/**/*.sql",
	"**/test/**/*.yaml",
	"**/test/**/*.xml",
    # "vendor/**",
    # "**autogen**",
  ]
}
