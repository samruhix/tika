use std::env;
use std::fs::OpenOptions;
use std::io::Write;
use std::thread;
use std::time::Duration;

const FRAME_1: &str = r#"
   \x1b[33m      .---.      \x1b[0m
   \x1b[33m     /     \     \x1b[0m   \x1b[1;36m♪ We're no strangers to love ♪\x1b[0m
   \x1b[33m    | () () |    \x1b[0m   \x1b[1;36m♪ You know the rules and so do I ♪\x1b[0m
   \x1b[33m     \  ^  /     \x1b[0m
   \x1b[35m   /|  |||  |\   \x1b[0m
   \x1b[35m  / |  |||  | \  \x1b[0m   \x1b[1;31m[!] UN TEST DE TIKA A ÉCHOUÉ !\x1b[0m
   \x1b[35m    |  |||  |    \x1b[0m
   \x1b[34m     /     \     \x1b[0m
   \x1b[34m    /       \    \x1b[0m
"#;

const FRAME_2: &str = r#"
   \x1b[33m      .---.      \x1b[0m
   \x1b[33m  \  /     \  /  \x1b[0m   \x1b[1;32m♪ Never gonna give you up ♪\x1b[0m
   \x1b[33m   \| () () |/   \x1b[0m   \x1b[1;32m♪ Never gonna let you down ♪\x1b[0m
   \x1b[33m     \  ▽  /     \x1b[0m   \x1b[1;33m♪ Never gonna run around and desert you ♪\x1b[0m
   \x1b[35m     | ||| |     \x1b[0m   \x1b[1;35m♪ Never gonna make you cry ♪\x1b[0m
   \x1b[35m     | ||| |     \x1b[0m   \x1b[1;36m♪ Never gonna say goodbye ♪\x1b[0m
   \x1b[35m     | ||| |     \x1b[0m   \x1b[1;31m♪ Never gonna tell a lie and hurt you ♪\x1b[0m
   \x1b[34m      /   \      \x1b[0m
   \x1b[34m     /     \     \x1b[0m
"#;

const RICK_PORTRAIT: &str = r#"
████████████████████████████████████████████████████████████
██                                                        ██
██   🎵  YOU JUST GOT RICKROLLED BY THE TIKA CI !  🎵    ██
██                                                        ██
██          ▄▄▄▄▄▄▄▄▄▄▄                                   ██
██       ▄▀▀           ▀▀▄    "Never gonna give you up,   ██
██      █   ▀▄▄     ▄▄▀   █    Never gonna let you down,  ██
██      █    (●)   (●)    █    Never gonna merge a build  ██
██      █        ▲        █    When a unit test falls     ██
██       █    ▀▄▄▄▄▄▀    █     down!"                     ██
██        ▀▄▄         ▄▄▀                                 ██
██        ▄██▀▀█████▀▀██▄                                 ██
██       ███   █████   ███    👉 https://youtu.be/dQw4w9WgXcQ
██      ████   █████   ████                               ██
██                                                        ██
████████████████████████████████████████████████████████████
"#;

fn render_ansi(frame: &str) -> String {
    frame.replace("\\x1b", "\x1b")
}

fn main() {
    let reason = env::var("INPUT_REASON")
        .unwrap_or_else(|_| "Un cas de test du cas d'étude a échoué !".to_string());

    println!("\n\x1b[1;31m============================================================\x1b[0m");
    println!("\x1b[1;33m🚨 ALERTE CI : {}\x1b[0m", reason);
    println!("\x1b[1;31m============================================================\x1b[0m");

    // Affichage des frames animées dans le terminal des logs GitHub Actions
    let frames = [FRAME_1, FRAME_2, FRAME_1, FRAME_2];
    for (idx, frame) in frames.iter().enumerate() {
        println!("\x1b[1;34m--- [Frame GIF Terminal {}/{}] ---\x1b[0m", idx + 1, frames.len());
        println!("{}", render_ansi(frame));
        thread::sleep(Duration::from_millis(150));
    }

    println!("\x1b[1;36m{}\x1b[0m", RICK_PORTRAIT);

    // Écriture d'un vrai GIF animé dans le résumé GitHub Actions ($GITHUB_STEP_SUMMARY)
    if let Ok(summary_path) = env::var("GITHUB_STEP_SUMMARY") {
        if let Ok(mut file) = OpenOptions::new().create(true).append(true).open(summary_path) {
            let markdown = format!(
                r#"## 🕺 RICKROLLED ! ({reason})

> **Un cas de test de `tika-core` a échoué !**

![Rick Astley Dancing GIF](https://media1.tenor.com/m/x8v1oNUOmg4AAAAd/rickroll-roll.gif)



🔗 **[Cliquez ici pour réparer les tests automatiquement](https://www.youtube.com/watch?v=dQw4w9WgXcQ)**
"#
            );
            let _ = file.write_all(markdown.as_bytes());
        }
    }
}
