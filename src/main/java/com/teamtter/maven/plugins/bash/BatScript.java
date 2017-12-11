package com.teamtter.maven.plugins.bash;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.maven.plugins.annotations.Parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatScript {

	@Parameter
	@Setter
	String script;


	boolean executableExists() {
		boolean existsInPath = Stream.of(System.getenv("PATH")
				.split(Pattern.quote(File.pathSeparator)))
				.map(Paths::get)
				.anyMatch(path -> {
					File dir = path.toFile();
					if (dir.isDirectory()) {
						File targetExecutable = new File(dir, executable);
						return targetExecutable.exists();
					} else {
						return false;
					}
				});
		return existsInPath;
	}

	public static void main(String[] args) {
		BatScript p = new BatScript();
		p.setExecutable("chmod2");
		System.out.println(p.executableExists());
	}

}
