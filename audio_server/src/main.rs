use actix_web::error::BlockingError;
use actix_web::error::Error as ActixError;
use actix_web::{middleware, web, App, HttpResponse, HttpServer};
use std::io::{Error, ErrorKind};
use std::{process::Command, sync::Arc};
use serde_json::json;

async fn toggle(name: web::Data<String>) -> Result<HttpResponse, ActixError> {
    execute_toggle(name.into_inner()).await?;
    Ok(HttpResponse::Ok().finish())
}

async fn status(name: web::Data<String>) -> Result<HttpResponse, ActixError> {
    let res = get_status(name.into_inner()).await?;
    Ok(HttpResponse::Ok().json(json!({ "muted": res})))
}

async fn execute_toggle(mic_name: Arc<String>) -> std::io::Result<()> {
    web::block::<_, (), std::io::Error>(move || {
        Command::new("SoundVolumeView.exe")
            .args(&["/Switch", &*mic_name])
            .output()?;
        Ok(())
    })
    .await
    .map_err(|e| match e {
        BlockingError::Error(e) => e,
        actix_web::error::BlockingError::Canceled => {
            Error::new(ErrorKind::Other, "Failed to block!")
        }
    })?;
    Ok(())
}

/// True means muted.
async fn get_status(mic_name: Arc<String>) -> std::io::Result<bool> {
    web::block::<_, bool, std::io::Error>(move || {
        let output = Command::new("SoundVolumeView.exe")
            .args(&["/GetMute", &*mic_name])
            .output()?
            .status
            .code()
            .ok_or(Error::new(ErrorKind::Other, "No code provided!"))?;
        Ok(output == 1)
    })
    .await
    .map_err(|e| match e {
        BlockingError::Error(e) => e,
        actix_web::error::BlockingError::Canceled => {
            Error::new(ErrorKind::Other, "Failed to block!")
        }
    })
}

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    std::env::set_var("RUST_LOG", "actix=info,actix_web=info");
    env_logger::init();

    let mut args = std::env::args();
    args.next();

    let mic_name = args.next().expect("No mic found.");
    let mic_name = web::Data::new(mic_name);

    HttpServer::new(move || {
        App::new()
            // enable logger
            .app_data(mic_name.clone())
            .wrap(middleware::Logger::default())
            .route("/toggle", web::get().to(toggle))
            .route("/status", web::get().to(status))
    })
    .bind("0.0.0.0:8080")?
    .run()
    .await
}
