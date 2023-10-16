import cv2
from pydub import AudioSegment
from pydub.silence import split_on_silence
import os
import subprocess
import zipfile
from moviepy.editor import VideoFileClip
from moviepy.editor import *
from tqdm import tqdm
import shutil

PATH = '절대경로 or 비워두기'

# 사용할 파일 로드
def load(cap_path, img_path, voi_path):
    #동영상(커버할 영상, 노래) 파일 로드
    cap = cv2.VideoFileClip(cap_path)

    #이미지(전신 사진) 파일 로드
    img = cv2.imread(img_path)

    #음성(커버할 목소리) 파일 로드
    voi = AudioSegment.from_file(voi_path)

    return cap, img, voi


#커버할 노래와 영상 분리
def cap_sep(PATH, cap):
    #1) 커버할 영상 소리 제거(mp4 -> mp4)
    os.makedirs(PATH + '/cover_video', exist_ok=True)
    new_clip = cap.without_audio()
    new_clip.write_videofile(PATH + '/cover_video/video_without_sound.mp4')

    #2) 커버할 영상에서 음성 추출(mp4 -> mp3)
    os.makedirs(PATH + '/cover_music')
    cap.audio.write_audiofile(PATH + '/cover_music/cover_audio.mp3')

    #3) 커버할 노래 mr 제거 (mp3 -> wav)
    cover_audio = AudioSegment.from_mp3(PATH + '/cover_music/cover_audio.mp3')

    # 가상 MR 파일 (비어있는 오디오 세그먼트) 생성
    empty_audio = AudioSegment.silent(duration=len(cover_audio))

    # MR 제거된 오디오 생성
    cover_audio_without_mr = cover_audio - empty_audio

    # MR만 들어있는 오디오 생성
    cover_audio_mr = empty_audio

    # MR 제거된 오디오를 WAV로 저장
    cover_audio_without_mr.export(PATH + '/cover_music/cover_audio_without_mr.wav', format='wav')

    # MR만 들어있는 오디오를 WAV로 저장
    cover_audio_mr.export(PATH + '/cover_music/cover_audio_mr.wav', format='wav')


#음성(커버할 목소리) 1초~15초로 자르기 && 무음 구간 제거
def voice_sep(PATH, sound):
    sound = sound.set_frame_rate(44100).set_channels(1).set_sample_width(2)

    # 무음 구간을 기준으로 오디오 파일 분리 및 무음 제거
    audio_chunks = split_on_silence(sound,
        min_silence_len=1000,  # 최소 무음 길이 (밀리초 단위)
        silence_thresh=-35,  # 무음으로 간주되는 dBFS 값
        keep_silence=500  # 분리된 오디오 조각들 간의 추가적인 무음 길이 (밀리초 단위)
    )

    # 출력 파일명 설정 및 출력 폴더 생성
    os.makedirs(PATH + '/user_voice', exist_ok=True)

    # Save audio chunks within 1-15 seconds
    for i, chunk in enumerate(audio_chunks):
        if 1000 < len(chunk) <= 15000:  # Ensure it's between 1-15 seconds
            output_file = os.path.join(PATH + '/user_voice', f'chunk_{i}.wav')
            chunk.export(output_file, format='wav')

    # Zip the saved audio chunks
    file_path = PATH + '/user_voice'
    zip_file = zipfile.ZipFile(file_path + "/user_voice_wav.zip", "w")
    for (path, _, files) in os.walk(file_path):
        for file in files:
            if file.endswith('.wav'):
                zip_file.write(os.path.join(path, file), compress_type=zipfile.ZIP_DEFLATED)
    zip_file.close()

def data_preprocessing(PATH):
    cap_path = PATH + '/' + '파일명.m4a'
    img_path = PATH + '/' + '파일명' #이거는 확장자명 아직 모른다.
    voi_path = PATH + '/' + '파일명.m4a'

    video, image, voice = load(cap_path, img_path, voi_path)
    cap_sep(PATH, video)
    voice_sep(PATH, voice)
