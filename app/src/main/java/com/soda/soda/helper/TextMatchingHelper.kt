package com.soda.soda.helper

import org.tensorflow.lite.support.label.Category

object TextMatchingHelper {
    fun textMatch(category: Category): String{
        var label: String = ""

        // 카테고리 정보가 Speech일 경우, 화면에 "말하기소리 같습니다." 메시지를 표시
        // 카테고리 정보가 Child speech, kid speaking일 경우, 화면에 "어린이 말하기, 어린이 말하기소리 같습니다." 메시지를 표시
        // ... (여러 분류 결과에 대해 각각 메시지를 할당함)
        label = when(category.label) {
            "Speech" -> "말하는 소리 같습니다."
            "Child speech, kid speaking" -> "어린 아이가 말하는 소리 같습니다."
            "Conversation" -> "대화 소리 같습니다."
            "Narration, monologue" -> "내레이션 소리 같습니다."
            "Babbling" -> "아기 옹알이 소리 같습니다."
            "Speech synthesizer" -> "합성 음성 같습니다."
            "Shout" -> "고함 소리 같습니다."
            "Bellow" -> "울부짖는 소리 같습니다."
            "Whoop" -> "환호하는 소리 같습니다."
            "Yell" -> "고함 소리 같습니다."
            "Children shouting" -> "어린 아이 비명 소리 같습니다."
            "Screaming" -> "비명 소리 같습니다."
            "Whispering" -> "속삭이는 소리 같습니다."
            "Laughter" -> "웃음소리 같습니다."
            "Baby laughter" -> "아기 웃음소리 같습니다."
            "Giggle" -> "낄낄거리며 웃는 소리 같습니다."
            "Snicker" -> "숨죽여 낄낄 웃는 소리 같습니다."
            "Belly laugh" -> "크게 웃음소리 같습니다."
            "Chuckle, chortle" -> "깔깔거리며 웃는 소리 같습니다."
            "Crying, sobbing" -> "울음소리 같습니다."
            "Baby cry, infant cry" -> "아기 울음소리 같습니다."
            "Whimper" -> "흐느껴 우는소리 같습니다."
            "Wail, moan" -> "통곡하는 소리 같습니다."
            "Sigh" -> "한숨 소리 같습니다."
            "Singing" -> "노래 소리 같습니다."
            "Choir" -> "합창단 소리 같습니다."
            "Yodeling" -> "요들송 소리 같습니다."
            "Chant" -> "노래 소리 같습니다."
            "Mantra" -> "만트라(불교음악) 소리 같습니다."
            "Child singing" -> "어린이 노래 소리 같습니다."
            "Synthetic singing" -> "합성 음성 노래 소리 같습니다."
            "Rapping" -> "랩을 하는 것 같습니다."
            "Humming" -> "허밍 소리 같습니다."
            "Groan" -> "신음 소리 같습니다."
            "Grunt" -> "돼지가 꿀꿀거리는 소리 같습니다."
            "Whistling" -> "휘파람 소리 같습니다."
            "Breathing" -> "호흡 소리 같습니다."
            "Wheeze" -> "씨근거리는(천식) 소리 같습니다."
            "Snoring" -> "코골이 소리 같습니다."
            "Gasp" -> "숨을 헐떡거리는 소리 같습니다."
            "Pant" -> "숨을 헐떡거리는 소리 같습니다."
            "Snort" -> "콧김 소리 같습니다."
            "Cough" -> "기침 소리 같습니다."
            "Throat clearing" -> "목을 가다듬는 소리 같습니다."
            "Sneeze" -> "재채기 소리 같습니다."
            "Sniff" -> "코를 킁킁거리는 소리 같습니다."
            "Run" -> "달리는 소리 같습니다."
            "Shuffle" -> "발을 질질 끄는 소리 같습니다."
            "Walk, footsteps" -> "걷는 소리 같습니다."
            "Chewing, mastication" -> "씹는 소리 같습니다."
            "Biting" -> "물어뜯는 소리 같습니다."
            "Gargling" -> "가글 소리 같습니다."
            "Stomach rumble" -> "배가 꾸르륵거리는 소리 같습니다."
            "Burping, eructation" -> "트림 소리 같습니다."
            "Hiccup" -> "딸꾹질 소리 같습니다."
            "Fart" -> "방귀 소리 같습니다."
            "Hands" -> "손뼉치는 소리 같습니다."
            "Finger snapping" -> "손가락 스냅 소리 같습니다."
            "Clapping" -> "박수 소리 같습니다."
            "Heart sounds, heartbeat" -> "심장 박동 소리 같습니다."
            "Heart murmur" -> "심장에서 흐르는 혈액 소리 같습니다."
            "Cheering" -> "응원 소리 같습니다."
            "Applause" -> "박수 소리 같습니다."
            "Chatter" -> "수다 소리 같습니다."
            "Crowd" -> "군중 소리 같습니다."
            "Hubbub, speech noise, speech babble" -> "재잘재잘 떠드는 소리 같습니다."
            "Children playing" -> "노는 아이들 소리 같습니다."
            "Animal" -> "동물 소리 같습니다."
            "Domestic animals, pets" -> "동물(가축,애완동물) 소리 같습니다."
            "Dog" -> "개 소리 같습니다."
            "Bark" -> "개 짖는 소리 같습니다."
            "Yip" -> "깨갱 소리 같습니다."
            "Howl" -> "하울링하는 소리 같습니다."
            "Bow-wow" -> "멍멍 짖는 소리 같습니다."
            "Growling" -> "으르렁 거리는 소리 같습니다."
            "Whimper (dog)" -> "개가 낑낑거리는 소리 같습니다."
            "Cat" -> "고양이 소리 같습니다."
            "Purr" -> "고양이가 그르릉거리는 소리 같습니다."
            "Meow" -> "고양이 소리 같습니다."
            "Hiss" -> "뱀이 쉿 소리를 내는 것 같습니다."
            "Caterwaul" -> "고양이 소리 같습니다."
            "Livestock, farm animals, working animals" -> "가축 울음소리 같습니다."
            "Horse" -> "말이 우는 소리 같습니다."
            "Clip-clop" -> "말 발굽 소리 같습니다."
            "Neigh, whinny" ->  "말이 우는 소리 같습니다."
            "Cattle, bovinae" -> "소가 우는 소리 같습니다."
            "Moo" -> "음메하는 소리 같습니다."
            "Cowbell" -> "소 종소리 같습니다."
            "Pig" -> "꿀꿀거리는 소리 같습니다."
            "Oink" -> "꿀꿀거리는 소리 같습니다."
            "Goat" -> "염소 소리 같습니다."
            "Bleat" -> "염소가 메에하는 소리 같습니다."
            "Sheep" -> "양 울음소리 같습니다."
            "Fowl" -> "암탉 울음소리 같습니다."
            "Chicken, rooster" -> "닭 울음소리 같습니다."
            "Cluck" ->  "닭 울음소리 같습니다."
            "Crowing, cock-a-doodle-doo" -> "닭 울음소리 같습니다."
            "Turkey" -> "칠면조 소리 같습니다."
            "Gobble" -> "칠면조가 골골거리는 소리 같습니다."
            "Duck" -> "오리 울음소리 같습니다."
            "Quack" -> "꽥꽥 소리 같습니다."
            "Goose" -> "거위 소리 같습니다."
            "Honk" -> "기러기 울음 소리 같습니다."
            "Wild animals" -> "야생 동물 울음소리 같습니다."
            "Roaring cats (lions, tigers)" -> "포효하는 호랑이 소리 같습니다."
            "Roar" -> "동물이 울부짖는 소리 같습니다."
            "Bird" -> "새 울음소리 같습니다."
            "Bird vocalization, bird call, bird song" -> "새 울음소리 같습니다."
            "Chirp, tweet" -> "새가 지저귀는 소리 같습니다."
            "Squawk" -> "갈매기가 거억거억 우는소리 같습니다."
            "Pigeon, dove" -> "비둘기 울음소리 같습니다."
            "Coo" -> "비둘기 울음소리 같습니다."
            "Crow" -> "까마귀 울음소리 같습니다."
            "Caw" -> "까마귀 울음소리 같습니다."
            "Owl" -> "부엉이 울음소리 같습니다."
            "Hoot" ->"부엉이 울음소리 같습니다."
            "Bird flight, flapping wings" -> "새의 날갯짓 소리 같습니다."
            "Canidae, dogs, wolves" -> "늑대 울음소리 같습니다."
            "Rodents, rats, mice" -> "쥐 울음 소리 같습니다."
            "Mouse" -> "쥐 울음 소리 같습니다."
            "Patter" -> "물을 철벅철벅 튀기는 같습니다."
            "Insect" -> "곤충 울음소리 같습니다."
            "Cricket" -> "귀뚜라미 소리 같습니다."
            "Mosquito" -> "모기 소리 같습니다."
            "Fly, housefly" -> "파리 소리 같습니다."
            "Buzz" -> "벌레 소리 같습니다."
            "Bee, wasp, etc." -> "벌 소리 같습니다."
            "Frog" -> "개구리 소리 같습니다."
            "Croak" -> "개구리 소리 같습니다."
            "Snake" -> "뱀 소리 같습니다."
            "Rattle" -> "문이 덜걱거리는 소리 같습니다."
            "Whale vocalization" -> "고래 울음소리 같습니다."
            "Music" -> "음악 소리 같습니다."
            "Musical instrument" -> "악기 소리 같습니다."
            "Plucked string instrument" -> "현악기 소리 같습니다."
            "Guitar" -> "기타 소리 같습니다."
            "Electric guitar" -> "일렉트릭 기타 소리 같습니다."
            "Bass guitar" -> "베이스 기타 소리 같습니다."
            "Acoustic guitar" -> "어쿠스틱 기타 소리 같습니다."
            "Steel guitar, slide guitar" -> "철제 기타 소리 같습니다."
            "Tapping (guitar technique)" -> "탭핑(기타 테크닉) 소리 같습니다."
            "Strum" -> "현악기를 가볍게 연주하는 소리 같습니다."
            "Banjo" -> "밴조(현악기) 소리 같습니다."
            "Sitar" -> "시타르(현악기) 소리 같습니다."
            "Mandolin" -> "만돌린(현악기) 소리 같습니다."
            "Zither" -> "징거(현악기) 소리 같습니다."
            "Ukulele" -> "우쿨렐레 소리 같습니다."
            "Keyboard (musical)" -> "키보드(악기) 소리 같습니다."
            "Piano" -> "피아노 소리 같습니다."
            "Electric piano" -> "전자 피아노 소리 같습니다."
            "Organ" -> "오르간 소리 같습니다."
            "Electronic organ" -> "전자 오르간 소리 같습니다."
            "Hammond organ" -> "해먼드 오르간 소리 같습니다."
            "Synthesizer" -> "신디사이저 소리 같습니다."
            "Sampler" -> "샘플러(가상악기) 소리 같습니다."
            "Harpsichord" -> "하프시코드(악기) 소리 같습니다."
            "Percussion" -> "퍼커션(타악기) 소리 같습니다."
            "Drum kit" -> "드럼 소리 같습니다."
            "Drum machine" -> "전자 드럼소리 같습니다."
            "Drum" -> "드럼 소리 같습니다."
            "Snare drum" -> "스네어 드럼 소리 같습니다."
            "Rimshot" -> "타악기 연주기법 (림샷) 소리 같습니다."
            "Drum roll" -> "드럼 연타 소리 같습니다."
            "Bass drum" -> "베이스 드럼 소리 같습니다."
            "Timpani" -> "팀파니(타악기) 소리 같습니다."
            "Tabla" -> "타블라(타악기) 소리 같습니다."
            "Cymbal" -> "심벌즈 소리 같습니다."
            "Hi-hat" -> "하이햇(드럼) 소리 같습니다."
            "Wood block" -> "우드 블록(타악기) 소리 같습니다."
            "Tambourine" -> "탬버린 소리 같습니다."
            "Rattle (instrument)" -> "딸랑이(악기) 소리 같습니다."
            "Maraca" -> "마라카(악기) 소리 같습니다."
            "Gong" -> "징(악기) 소리 같습니다."
            "Tubular bells" -> "관형 종 소리 같습니다."
            "Mallet percussion" -> "말렛 (타악기) 소리 같습니다."
            "Marimba, xylophone" -> "마림바, 실로폰 소리 같습니다."
            "Glockenspiel" -> "글로켄슈필(악기) 소리 같습니다."
            "Vibraphone" -> "비브라폰(악기) 소리 같습니다."
            "Steelpan" -> "스틸팬(악기) 소리 같습니다."
            "Orchestra" -> "오케스트라 소리 같습니다."
            "Brass instrument" -> "금관악기 소리 같습니다."
            "French horn" -> "프렌치 호른(관악기) 소리 같습니다."
            "Trumpet" -> "트럼펫 소리 같습니다."
            "Trombone" -> "트롬본 소리 같습니다."
            "Bowed string instrument" -> "현악기 소리 같습니다."
            "String section" -> "현악기 소리 같습니다."
            "Violin, fiddle" -> "바이올린, 바이올린 소리 같습니다."
            "Pizzicato" -> "현악기를 손으로 뜯는 소리 같습니다."
            "Cello" -> "첼로 소리 같습니다."
            "Double bass" -> "더블베이스 소리 같습니다."
            "Wind instrument, woodwind instrument" -> "관악기, 목관악기 소리 같습니다."
            "Flute" -> "플룻 소리 같습니다."
            "Saxophone" -> "색소폰 소리 같습니다."
            "Clarinet" -> "클라리넷 소리 같습니다."
            "Harp" -> "하프 소리 같습니다."
            "Bell" -> "종 소리 같습니다."
            "Church bell" -> "교회 종 소리 같습니다."
            "Jingle bell" -> "징글벨 소리 같습니다."
            "Bicycle bell" -> "자전거 벨 소리 같습니다."
            "Tuning fork" -> "소리굽쇠 소리 같습니다."
            "Chime" -> "차임(종) 소리 같습니다."
            "Wind chime" -> "윈드 차임 (종) 소리 같습니다."
            "Change ringing (campanology)" -> "전통 종소리 기법 소리 같습니다."
            "Harmonica" -> "하모니카 소리 같습니다."
            "Accordion" -> "아코디언 소리 같습니다."
            "Bagpipes" -> "백파이프(악기) 소리 같습니다."
            "Didgeridoo" -> "디저리두(악기) 소리 같습니다."
            "Shofar" -> "뿔피리 소리 같습니다."
            "Theremin" -> "테레민(진공관 악기) 소리 같습니다."
            "Singing bowl" -> "싱잉 볼(명상도구) 소리 같습니다."
            "Scratching (performance technique)" -> "스크래칭(연주기법) 소리 같습니다."
            "Pop music" -> "팝 음악 소리 같습니다."
            "Hip hop music" -> "힙합 음악 소리 같습니다."
            "Beatboxing" -> "비트박스 소리 같습니다."
            "Rock music" -> "록 음악 소리 같습니다."
            "Heavy metal" -> "헤비메탈 소리 같습니다."
            "Punk rock" -> "펑크 록 소리 같습니다."
            "Grunge" -> "그런지 (밴드음악장르) 소리 같습니다."
            "Progressive rock" -> "프로그레시브 록 (밴드음악장르) 소리 같습니다."
            "Rock and roll" -> "락앤롤 (밴드음악장르) 소리 같습니다."
            "Psychedelic rock" -> "사이키델릭 록 (밴드음악장르) 소리 같습니다."
            "Rhythm and blues" -> "R&B 음악 소리 같습니다."
            "Soul music" -> "소울음악 (음악장르) 소리 같습니다."
            "Reggae" -> "레게음악 소리 같습니다."
            "Country" -> "컨트리음악 소리 같습니다."
            "Swing music" -> "스윙 음악 소리 같습니다."
            "Bluegrass" -> "블루그래스 (음악장르) 소리 같습니다."
            "Funk" -> "펑크 음악 소리 같습니다."
            "Folk music" -> "포크송(음악) 소리 같습니다."
            "Middle Eastern music" -> "중동 음악 소리 같습니다."
            "Jazz" -> "재즈 음악 소리 같습니다."
            "Disco" -> "디스코 음악 소리 같습니다."
            "Classical music" -> "클래식 음악 소리 같습니다."
            "Opera" -> "오페라 소리 같습니다."
            "Electronic music" -> "일렉트로닉 음악 소리 같습니다."
            "House music" -> "하우스 음악 소리 같습니다."
            "Techno" -> "테크노 음악 소리 같습니다."
            "Dubstep" -> "덥스텝 음악 소리 같습니다."
            "Drum and bass" -> "드럼 및 베이스 소리 같습니다."
            "Electronica" -> "일렉트로니카 음악 소리 같습니다."
            "Electronic dance music" -> "일렉트로닉 댄스 음악 소리 같습니다."
            "Ambient music" -> "편안한 배경음악 소리 같습니다."
            "Trance music" -> "전자음악 소리 같습니다."
            "Music of Latin America" -> "라틴 음악 소리 같습니다."
            "Salsa music" -> "살사(라틴) 음악 소리 같습니다."
            "Flamenco" -> "플라멩코(전통음악) 소리 같습니다."
            "Blues" -> "블루스 음악 소리 같습니다."
            "Music for children" -> "어린이를 위한 음악 소리 같습니다."
            "New-age music" -> "뉴에이지 음악 소리 같습니다."
            "Vocal music" -> "성악 음악 소리 같습니다."
            "A capella" -> "아카펠라 음악 소리 같습니다."
            "Music of Africa" -> "아프리카 음악 소리 같습니다."
            "Afrobeat" -> "아프리카 음악 소리 같습니다."
            "Christian music" -> "기독교 음악 소리 같습니다."
            "Gospel music" -> "가스펠 음악 소리 같습니다."
            "Music of Asia" -> "아시아 음악 소리 같습니다."
            "Carnatic music" -> "인도 전통음악 소리 같습니다."
            "Music of Bollywood" -> "발리우드(인도) 음악 소리 같습니다."
            "Ska" -> "레게 음악 소리 같습니다."
            "Traditional music" -> "전통 음악 소리 같습니다."
            "Independent music" -> "독립 음악 소리 같습니다."
            "Song" -> "노래 소리 같습니다."
            "Background music" -> "배경 음악 소리 같습니다."
            "Theme music" -> "테마 음악 소리 같습니다."
            "Jingle (music)" -> "중독성 있는 광고 음악 소리 같습니다."
            "Soundtrack music" -> "사운드트랙 음악 소리 같습니다."
            "Lullaby" -> "자장가 소리 같습니다."
            "Video game music" -> "비디오 게임 음악 소리 같습니다."
            "Christmas music" -> "캐롤 음악 소리 같습니다."
            "Dance music" -> "댄스 음악 소리 같습니다."
            "Wedding music" -> "결혼식 음악 소리 같습니다."
            "Happy music" -> "행복한 음악 소리 같습니다."
            "Sad music" -> "슬픈 음악 소리 같습니다."
            "Tender music" -> "부드러운 음악 소리 같습니다."
            "Exciting music" -> "신나는 음악 소리 같습니다."
            "Angry music" -> "화난 음악 소리 같습니다."
            "Scary music" -> "무서운 음악 소리 같습니다."
            "Wind" -> "바람 소리 같습니다."
            "Rustling leaves" -> "바스락거리는 나뭇잎 소리 같습니다."
            "Wind noise (microphone)" -> "마이크에 바람을 부는 소리 같습니다."
            "Thunderstorm" -> "천둥 소리 같습니다."
            "Thunder" -> "천둥 소리 같습니다."
            "Water" -> "물 소리 같습니다."
            "Rain" -> "비 소리 같습니다."
            "Raindrop" -> "빗방울 소리 같습니다."
            "Rain on surface" -> "물체 표면에 내리는 비 소리 같습니다."
            "Stream" -> "시냇물 흐르는 소리 같습니다."
            "Waterfall" -> "폭포 소리 같습니다."
            "Ocean" -> "바다 소리 같습니다."
            "Waves, surf" -> "파도, 서핑 소리 같습니다."
            "Steam" -> "증기 소리 같습니다."
            "Gurgling" -> "물이 흐르는 소리 같습니다."
            "Fire" -> "불 소리 같습니다."
            "Crackle" -> "부서지는 소리 같습니다."
            "Vehicle" -> "차량 소리 같습니다."
            "Boat, Water vehicle" -> "보트 소리 같습니다."
            "Sailboat, sailing ship" -> "요트 소리 같습니다."
            "Rowboat, canoe, kayak" -> "노 젓는 배 (카누) 소리 같습니다."
            "Motorboat, speedboat" -> "모터보트 소리 같습니다."
            "Ship" -> "선박소리 같습니다."
            "Motor vehicle (road)" -> "도로의 자동차소리 같습니다."
            "Car" -> "자동차소리 같습니다."
            "Vehicle horn, car horn, honking" -> "자동차 경적 소리 같습니다."
            "Toot" -> "피리를 부는 소리 같습니다."
            "Car alarm" -> "자동차 도난 경보 소리 같습니다."
            "Power windows, electric windows" -> "자동차 창문 여닫는 소리 같습니다."
            "Skidding" -> "자동차가 미끄러지는 소리 같습니다."
            "Tire squeal" -> "타이어가 급정거로 땅과 마찰하는 소리 같습니다."
            "Car passing by" -> "휙 지나가는 자동차 소리 같습니다."
            "Race car, auto racing" -> "자동차 경주 소리 같습니다."
            "Truck" -> "트럭 소리 같습니다."
            "Air brake" -> "대형 차량 제동시스템 소리 같습니다."
            "Air horn, truck horn" -> "트럭 경적 소리 같습니다."
            "Reversing beeps" -> "자동차 후진 경고음 소리 같습니다."
            "Ice cream truck, ice cream van" -> "아이스크림 트럭 소리 같습니다."
            "Bus" -> "버스 소리 같습니다."
            "Emergency vehicle" -> "긴급 차량 소리 같습니다."
            "Police car (siren)" -> "경찰차 사이렌 소리 같습니다."
            "Ambulance (siren)" -> "구급차 사이렌 소리 같습니다."
            "Fire engine, fire truck (siren)" -> "소방차 사이렌 소리 같습니다."
            "Motorcycle" -> "오토바이 소리 같습니다."
            "Traffic noise, roadway noise" -> "교통 소음, 도로 소음 소리 같습니다."
            "Rail transport" -> "철도 운송 소리 같습니다."
            "Train" -> "기차 소리 같습니다."
            "Train whistle" -> "기차 출발 알림 소리 같습니다."
            "Train horn" -> "기차 경적 소리 같습니다."
            "Railroad car, train wagon" -> "철도 차량 소리 같습니다."
            "Train wheels squealing" -> "기차 바퀴가 삐걱거리는 소리 같습니다."
            "Subway, metro, underground" -> "지하철 소리 같습니다."
            "Aircraft" -> "항공기 소리 같습니다."
            "Aircraft engine" -> "항공기 엔진 소리 같습니다."
            "Jet engine" -> "제트 엔진 소리 같습니다."
            "Propeller, airscrew" -> "프로펠러 소리 같습니다."
            "Helicopter" -> "헬리콥터 소리 같습니다."
            "Fixed-wing aircraft, airplane" -> "비행기 소리 같습니다."
            "Bicycle" -> "자전거 소리 같습니다."
            "Skateboard" -> "스케이트보드 소리 같습니다."
            "Engine" -> "엔진 소리 같습니다."
            "Light engine (high frequency)" -> "경량 엔진 소리 같습니다."
            "Dental drill, dentist's drill" -> "치과용 드릴 소리 같습니다."
            "Lawn mower" -> "잔디 깎는 기계 소리 같습니다."
            "Chainsaw" -> "전기톱 소리 같습니다."
            "Medium engine (mid frequency)" -> "중형 엔진 소리 같습니다."
            "Heavy engine (low frequency)" -> "대형 엔진 소리 같습니다."
            "Engine knocking" -> "엔진 작동 소리 같습니다."
            "Engine starting" -> "엔진 시동 소리 같습니다."
            "Idling" -> "엔진 공회전 소리 같습니다."
            "Accelerating, revving, vroom" -> "차량 가속 소리 같습니다."
            "Door" -> "문 소리 같습니다."
            "Doorbell" -> "초인종 소리 같습니다."
            "Ding-dong" -> "딩동 소리 같습니다."
            "Sliding door" -> "밀어서 여는 문 소리 같습니다."
            "Slam" -> "쾅 소리 같습니다."
            "Knock" -> "노크 소리 같습니다."
            "Tap" -> "두드림 소리 같습니다."
            "Squeak" -> "쥐가 찍찍거리는 소리 같습니다."
            "Cupboard open or close" -> "찬장 여닫는 소리 같습니다."
            "Drawer open or close" -> "서랍 여닫는 소리 같습니다."
            "Dishes, pots, and pans" -> "접시, 냄비, 프라이팬 소리 같습니다."
            "Cutlery, silverware" -> "식기류(수저) 소리 같습니다."
            "Chopping (food)" -> "음식을 다지는 소리 같습니다."
            "Frying (food)" -> "튀기는 소리 같습니다."
            "Microwave oven" -> "전자레인지 소리 같습니다."
            "Blender" -> "믹서기 소리 같습니다."
            "Water tap, faucet" -> "수도꼭지 소리 같습니다."
            "Sink (filling or washing)" -> "싱크대 소리 같습니다."
            "Bathtub (filling or washing)" -> "욕조 물 소리 같습니다."
            "Hair dryer" -> "헤어 드라이기 소리 같습니다."
            "Toilet flush" -> "변기 물 내리는 소리 같습니다."
            "Toothbrush" -> "칫솔질 소리 같습니다."
            "Electric toothbrush" -> "전동 칫솔 소리 같습니다."
            "Vacuum cleaner" -> "진공 청소기 소리 같습니다."
            "Zipper (clothing)" -> "지퍼(의류)올리는 소리 같습니다."
            "Keys jangling" -> "열쇠가 짤랑거리는 소리 같습니다."
            "Coin (dropping)" -> "동전 떨어지는 소리 같습니다."
            "Scissors" -> "가위 소리 같습니다."
            "Electric shaver, electric razor" -> "전동 면도기 소리 같습니다."
            "Shuffling cards" -> "카드 섞는 소리 같습니다."
            "Typing" -> "키보드 타이핑 소리 같습니다."
            "Typewriter" -> "타자기 소리 같습니다."
            "Computer keyboard" -> "컴퓨터 키보드 소리 같습니다."
            "Writing" -> "글쓰는 소리 같습니다."
            "Alarm" -> "알람 소리 같습니다."
            "Telephone" -> "전화 소리 같습니다."
            "Telephone bell ringing" -> "전화 벨 울림 소리 같습니다."
            "Ringtone" -> "벨 소리 같습니다."
            "Telephone dialing, DTMF" -> "전화 거는 소리 같습니다."
            "Dial tone" -> "발신음 소리 같습니다."
            "Busy signal" -> "통화 중 신호 소리 같습니다."
            "Alarm clock" -> "알람 시계 소리 같습니다."
            "Siren" -> "사이렌 소리 같습니다."
            "Civil defense siren" -> "민방위 사이렌 소리 같습니다."
            "Buzzer" -> "경보 소리 같습니다."
            "Smoke detector, smoke alarm" -> "경보 소리 같습니다."
            "Fire alarm" -> "화재 경보 소리 같습니다."
            "Foghorn" -> "경보 소리 같습니다."
            "Whistle" -> "호루라기 소리 같습니다."
            "Steam whistle" -> "증기 경보(선박) 소리 같습니다."
            "Mechanisms" -> "기계장치 소리 같습니다."
            "Ratchet, pawl" -> "톱니바퀴 소리 같습니다."
            "Clock" -> "시계 소리 같습니다."
            "Tick" -> "시계의 틱 소리 같습니다."
            "Tick-tock" -> "틱톡 소리 같습니다."
            "Gears" -> "기어 소리 같습니다."
            "Pulleys" -> "도르래 소리 같습니다."
            "Sewing machine" -> "재봉틀 소리 같습니다."
            "Mechanical fan" -> "선풍기 소리 같습니다."
            "Air conditioning" -> "에어컨 소리 같습니다."
            "Cash register" -> "현금 인출기 소리 같습니다."
            "Printer" -> "프린터 소리 같습니다."
            "Camera" -> "카메라 소리 같습니다."
            "Single-lens reflex camera" -> "카메라 소리 같습니다."
            "Tools" -> "도구 소리 같습니다."
            "Hammer" -> "망치 소리 같습니다."
            "Jackhammer" -> "잭해머 (건설 현장드릴) 소리 같습니다."
            "Sawing" -> "톱질 소리 같습니다."
            "Filing (rasp)" -> "랍스(공구)를 이용한 표면 다듬는 소리 같습니다."
            "Sanding" -> "물체 표면을 평탄화하는 소리 같습니다."
            "Power tool" -> "전동 공구 소리 같습니다."
            "Drill" -> "드릴 소리 같습니다."
            "Explosion" -> "폭발 소리 같습니다."
            "Gunshot, gunfire" -> "총 소리 같습니다."
            "Machine gun" -> "총 소리 같습니다."
            "Fusillade" -> "포격 소리 같습니다."
            "Artillery fire" -> "포격 소리 같습니다."
            "Cap gun" -> "총 소리 같습니다."
            "Fireworks" -> "폭죽 소리 같습니다."
            "Firecracker" -> "폭죽 소리 같습니다."
            "Burst, pop" -> "폭발 소리 같습니다."
            "Eruption" -> "폭발 소리 같습니다."
            "Boom" -> "폭발 소리 같습니다."
            "Wood" -> "나무 소리 같습니다."
            "Chop" -> "자르는 소리 같습니다."
            "Splinter" -> "쪼개지는 소리 같습니다."
            "Crack" -> "깨지는 소리 같습니다."
            "Glass" -> "유리 소리 같습니다."
            "Chink, clink" -> "깨지는 소리 같습니다."
            "Shatter" -> "깨지는 소리 같습니다."
            "Liquid" -> "액체 소리 같습니다."
            "Splash, splatter" -> "액체가 풍덩 튀는 같습니다."
            "Slosh" -> "퐁당(액체) 소리 같습니다."
            "Squish" -> "뭉개지는 소리 같습니다."
            "Drip" -> "물방울 떨어지는 소리 같습니다."
            "Pour" -> "물이 쏟아지는 소리 같습니다."
            "Trickle, dribble" -> "물방울 흘러내리는 소리 같습니다."
            "Gush" -> "물이 쏟아져 나오는 소리 같습니다."
            "Fill (with liquid)" -> "물을 채우는 소리 같습니다."
            "Spray" -> "스프레이 소리 같습니다."
            "Pump (liquid)" -> "물을 퍼올리는 펌프 소리 같습니다."
            "Stir" -> "액체를 젓는 소리 같습니다."
            "Boiling" -> "물을 끓는 소리 같습니다."
            "Sonar" -> "음파 탐지기 소리 같습니다."
            "Arrow" -> "화살 소리 같습니다."
            "Whoosh, swoosh, swish" -> "뭔가 빠르게 지나갈 때 나는 소리 같습니다."
            "Thump, thud" -> "부딪치는 소리 같습니다."
            "Thunk" ->"부딪치는 소리 같습니다."
            "Electronic tuner" -> "전자 조율기 소리 같습니다."
            "Effects unit" -> "음악 효과 장치 소리 같습니다."
            "Chorus effect" -> "코러스 효과 소리 같습니다."
            "Basketball bounce" -> "농구공 튕기는 소리 같습니다."
            "Bang" -> "총 소리 같습니다."
            "Slap, smack" -> "때리는 소리 같습니다."
            "Whack, thwack" -> "때리는 소리 같습니다."
            "Smash, crash" -> "부서지는 소리 같습니다."
            "Breaking" -> "부서지는 소리 같습니다."
            "Bouncing" -> "탄성물체를 튀기는 소리 같습니다."
            "Whip" -> "채찍 소리 같습니다."
            "Flap" -> "퍼덕거리는 소리 같습니다."
            "Scratch" -> "긁는 소리 같습니다."
            "Scrape" -> "긁는 소리 같습니다."
            "Rub" -> "문지르는 소리 같습니다."
            "Roll" -> "굴리는 소리 같습니다."
            "Crushing" -> "찌그러지는 소리 같습니다."
            "Crumpling, crinkling" -> "구겨지는 소리 같습니다."
            "Tearing" -> "찢어지는 소리 같습니다."
            "Beep, bleep" -> "삐, 삐 소리 같습니다."
            "Ping" -> "핑(효과음) 소리 같습니다."
            "Ding" -> "딩(효과음) 소리 같습니다."
            "Clang" -> "딸깍(효과음) 소리 같습니다."
            "Squeal" -> "비명 소리 같습니다."
            "Creak" -> "뭔가 스치면서 나는 소음 같습니다."
            "Rustle" -> "부드럽게 쉬쉬거리는 소리 같습니다."
            "Whir" -> "빠르게 회전하는 물체 소리 같습니다."
            "Clatter" -> "부딪치는 소리 같습니다."
            "Sizzle" -> "물체를 구울 때 지글거리는 소리 같습니다."
            "Clicking" -> "클릭 소리 같습니다."
            "Clickety-clack" -> "딸깍-딸깍 소리 같습니다."
            "Rumble" -> "저음이 진동하는 같습니다."
            "Plop" -> " 물에 무거운 물체가 퐁 떨어지는 소리 같습니다."
            "Jingle, tinkle" -> "작은 금속,유리가 부딪치는 소리 같습니다."
            "Hum" -> "전동 장치 등에서 발생하는 저음 소리 같습니다."
            "Zing" -> "빠르고 날카로운 소리 같습니다."
            "Boing" -> "탄성있는 물체가 튕기는 효과음 같습니다."
            "Crunch" -> "부서지는 소리 같습니다."
            "Silence" -> "고요한 상태인 것 같습니다"
            "Sine wave" -> "사인파 소리 같습니다."
            "Harmonic" -> "고주파 소리 같습니다."
            "Chirp tone" -> "연속적으로 변하는 주파수를 가진 소리 같습니다."
            "Sound effect" -> "음향 효과 소리 같습니다."
            "Pulse" -> "짧은 길이의 강한 신호 소리 같습니다."
            "Inside, small room" -> "작은 방 내부 소리 같습니다."
            "Inside, large room or hall" -> "큰 홀의 내부 소리 같습니다."
            "Inside, public space" -> "공공장소의 내부 소리 같습니다."
            "Outside, urban or manmade" -> "도시 소리 같습니다."
            "Outside, rural or natural" -> "시골 소리 같습니다."
            "Reverberation" -> "반향 효과 소리 같습니다."
            "Echo" -> "메아리 소리 같습니다."
            "Noise" -> "소음 소리 같습니다."
            "Environmental noise" -> "환경 소음 소리 같습니다."
            "Static" -> "잡음 혹은 노이즈 소리 같습니다."
            "Mains hum" -> "전기통신망의 저음(잡음) 같습니다."
            "Distortion" -> "소리가 왜곡되는 효과 소리 같습니다."
            "Sidetone" -> "통화 시 발생하는 자신의 음성 소리 같습니다."
            "Cacophony" -> "불협화음 소리 같습니다."
            "White noise" -> "화이트 노이즈 소리 같습니다."
            "Pink noise" -> "핑크 노이즈 소리 같습니다."
            "Throbbing" -> "고동치는 소리 같습니다."
            "Vibration" -> "진동 소리 같습니다."
            "Television" -> "텔레비전 소리 같습니다."
            "Radio" -> "라디오 소리 같습니다."
            "Field recording" -> "현장 녹음 소리 같습니다."
            else -> "무슨 소리인지 모르겠습니다."
        }
        return label
    }

}