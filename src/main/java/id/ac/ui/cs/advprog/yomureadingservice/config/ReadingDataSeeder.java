package id.ac.ui.cs.advprog.yomureadingservice.config;

import id.ac.ui.cs.advprog.yomureadingservice.reading.model.Category;
import id.ac.ui.cs.advprog.yomureadingservice.reading.model.Option;
import id.ac.ui.cs.advprog.yomureadingservice.reading.model.Question;
import id.ac.ui.cs.advprog.yomureadingservice.reading.model.Text;
import id.ac.ui.cs.advprog.yomureadingservice.reading.repository.CategoryRepository;
import id.ac.ui.cs.advprog.yomureadingservice.reading.repository.OptionRepository;
import id.ac.ui.cs.advprog.yomureadingservice.reading.repository.QuestionRepository;
import id.ac.ui.cs.advprog.yomureadingservice.reading.repository.TextRepository;

import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReadingDataSeeder implements CommandLineRunner {

    private final TextRepository textRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;

    public ReadingDataSeeder(
            TextRepository textRepository,
            CategoryRepository categoryRepository,
            QuestionRepository questionRepository,
            OptionRepository optionRepository
    ) {
        this.textRepository = textRepository;
        this.categoryRepository = categoryRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (textRepository.count() == 0) {
            Category literasiDigital = categoryRepository.save(new Category("Literasi Digital"));
            Category lingkungan = categoryRepository.save(new Category("Lingkungan"));
            Category kesehatan = categoryRepository.save(new Category("Kesehatan"));
            Category pendidikan = categoryRepository.save(new Category("Pendidikan"));
            Category ekonomi = categoryRepository.save(new Category("Ekonomi"));
            Category teknologi = categoryRepository.save(new Category("Teknologi"));
            Category budaya = categoryRepository.save(new Category("Budaya"));
            Category kewarganegaraan = categoryRepository.save(new Category("Kewarganegaraan"));

            seedText(
                    literasiDigital,
                    "Memahami Hoaks di Era Digital",
                    "Hoaks adalah informasi yang tidak benar, tetapi dibuat seolah-olah benar agar dipercaya oleh banyak orang. Di era digital, hoaks dapat menyebar dengan sangat cepat melalui media sosial, grup percakapan, dan berbagai platform berbagi informasi. Banyak orang membagikan informasi tanpa membaca keseluruhan isi berita atau memeriksa sumbernya terlebih dahulu.\n\n" +
                    "Dampak hoaks tidak bisa dianggap sepele karena dapat menimbulkan kepanikan, merusak reputasi seseorang, bahkan memecah belah masyarakat. Oleh karena itu, setiap pengguna internet perlu memiliki kemampuan literasi digital. Literasi digital membantu seseorang untuk memeriksa kebenaran informasi, mengenali sumber tepercaya, dan tidak mudah terpengaruh oleh judul yang provokatif.",
                    "admin-1",
                    List.of(
                            new QuestionSeed("Apa yang dimaksud dengan hoaks?", "Informasi palsu yang dibuat seolah-olah benar", "Informasi resmi dari pemerintah", "Pendapat pribadi yang selalu benar", "Berita lama yang dipublikasikan ulang", "A"),
                            new QuestionSeed("Mengapa hoaks mudah menyebar di era digital?", "Karena banyak orang membagikan informasi tanpa verifikasi", "Karena semua berita di internet pasti benar", "Karena media sosial melarang pengguna membaca berita", "Karena informasi digital tidak bisa disalin", "A"),
                            new QuestionSeed("Apa salah satu dampak buruk dari hoaks?", "Menimbulkan kepanikan di masyarakat", "Meningkatkan kemampuan membaca", "Membuat informasi lebih akurat", "Mengurangi konflik sosial", "A"),
                            new QuestionSeed("Kemampuan apa yang dibutuhkan agar tidak mudah tertipu hoaks?", "Literasi digital", "Kemampuan menggambar", "Kecepatan mengetik", "Kemampuan berbelanja online", "A"),
                            new QuestionSeed("Apa yang sebaiknya dilakukan sebelum membagikan informasi?", "Memeriksa sumber dan kebenaran informasi", "Langsung membagikannya ke semua grup", "Mengubah judul agar lebih menarik", "Menghapus nama penulis berita", "A")
                    )
            );

            seedText(
                    lingkungan,
                    "Pentingnya Mengurangi Sampah Plastik",
                    "Sampah plastik merupakan salah satu masalah lingkungan yang paling sulit diatasi. Plastik membutuhkan waktu sangat lama untuk terurai secara alami, bahkan dapat bertahan ratusan tahun di tanah maupun laut. Ketika plastik mencemari lingkungan, hewan dapat salah mengira plastik sebagai makanan sehingga membahayakan kehidupan mereka.\n\n" +
                    "Upaya mengurangi sampah plastik dapat dimulai dari kebiasaan sehari-hari. Masyarakat dapat membawa tas belanja sendiri, menggunakan botol minum isi ulang, dan menghindari penggunaan sedotan plastik sekali pakai. Perubahan kecil yang dilakukan secara konsisten oleh banyak orang dapat membantu mengurangi beban lingkungan secara signifikan.",
                    "admin-1",
                    List.of(
                            new QuestionSeed("Mengapa sampah plastik menjadi masalah lingkungan?", "Karena sulit terurai secara alami", "Karena mudah menjadi pupuk", "Karena selalu dapat dimakan hewan", "Karena hanya ditemukan di rumah", "A"),
                            new QuestionSeed("Berapa lama plastik dapat bertahan di lingkungan?", "Ratusan tahun", "Beberapa menit", "Satu hari", "Satu minggu", "A"),
                            new QuestionSeed("Apa dampak plastik terhadap hewan?", "Hewan dapat salah mengira plastik sebagai makanan", "Hewan menjadi lebih sehat", "Hewan dapat menggunakan plastik sebagai obat", "Hewan tidak pernah terkena dampaknya", "A"),
                            new QuestionSeed("Apa contoh kebiasaan mengurangi sampah plastik?", "Membawa tas belanja sendiri", "Menggunakan plastik baru setiap hari", "Membuang plastik ke sungai", "Membakar plastik di ruang tertutup", "A"),
                            new QuestionSeed("Mengapa perubahan kecil tetap penting?", "Karena jika dilakukan banyak orang dapat berdampak besar", "Karena tidak membutuhkan aturan apa pun", "Karena langsung menghilangkan semua sampah", "Karena membuat plastik cepat terurai", "A")
                    )
            );

            seedText(
                    kesehatan,
                    "Manfaat Tidur yang Cukup",
                    "Tidur yang cukup merupakan kebutuhan dasar manusia yang sering diabaikan. Saat tidur, tubuh melakukan proses pemulihan, memperbaiki sel, dan mengatur kembali energi yang digunakan sepanjang hari. Kurang tidur dapat membuat seseorang sulit berkonsentrasi, mudah lelah, dan lebih rentan mengalami gangguan kesehatan.\n\n" +
                    "Selain berpengaruh pada kondisi fisik, tidur juga berkaitan erat dengan kesehatan mental. Orang yang memiliki pola tidur teratur cenderung lebih stabil secara emosional dan mampu mengelola stres dengan lebih baik. Oleh karena itu, menjaga jadwal tidur, mengurangi penggunaan gawai sebelum tidur, dan menciptakan suasana kamar yang nyaman merupakan langkah penting untuk meningkatkan kualitas tidur.",
                    "admin-1",
                    List.of(
                            new QuestionSeed("Apa fungsi tidur bagi tubuh?", "Membantu proses pemulihan tubuh", "Menghentikan semua aktivitas tubuh secara permanen", "Mengurangi kebutuhan makan selamanya", "Membuat tubuh tidak perlu bergerak lagi", "A"),
                            new QuestionSeed("Apa akibat kurang tidur?", "Sulit berkonsentrasi dan mudah lelah", "Selalu merasa lebih segar", "Tidak membutuhkan istirahat", "Meningkatkan daya ingat secara instan", "A"),
                            new QuestionSeed("Selain fisik, tidur juga berpengaruh pada apa?", "Kesehatan mental", "Warna rambut", "Ukuran sepatu", "Tinggi badan orang dewasa secara instan", "A"),
                            new QuestionSeed("Apa kebiasaan yang dapat meningkatkan kualitas tidur?", "Mengurangi penggunaan gawai sebelum tidur", "Minum kopi berlebihan sebelum tidur", "Tidur di tempat yang sangat bising", "Mengubah jam tidur setiap hari", "A"),
                            new QuestionSeed("Mengapa jadwal tidur perlu dijaga?", "Agar tubuh memiliki pola istirahat yang teratur", "Agar tubuh tidak pernah merasa mengantuk", "Agar seseorang tidak perlu olahraga", "Agar bisa begadang setiap malam", "A")
                    )
            );

            seedText(
                    pendidikan,
                    "Belajar Efektif dengan Manajemen Waktu",
                    "Manajemen waktu adalah kemampuan untuk mengatur kegiatan agar tujuan dapat tercapai secara lebih terarah. Dalam kegiatan belajar, manajemen waktu membantu siswa menentukan prioritas, membagi waktu antara membaca materi, mengerjakan tugas, dan beristirahat. Tanpa pengaturan waktu yang baik, seseorang dapat merasa kewalahan meskipun tugas yang dimiliki sebenarnya masih dapat diselesaikan.\n\n" +
                    "Belajar efektif tidak selalu berarti belajar dalam waktu yang sangat lama. Cara yang lebih baik adalah belajar secara terencana, membuat jadwal, dan menggunakan teknik seperti belajar bertahap. Dengan membagi materi menjadi bagian kecil, siswa dapat memahami isi pelajaran secara lebih mendalam dan menghindari kebiasaan belajar mendadak sebelum ujian.",
                    "admin-1",
                    List.of(
                            new QuestionSeed("Apa yang dimaksud dengan manajemen waktu?", "Kemampuan mengatur kegiatan agar tujuan tercapai", "Kemampuan menghindari semua tugas", "Kebiasaan menunda pekerjaan", "Cara belajar hanya saat ujian", "A"),
                            new QuestionSeed("Mengapa manajemen waktu penting dalam belajar?", "Karena membantu menentukan prioritas", "Karena membuat tugas hilang sendiri", "Karena membuat siswa tidak perlu membaca", "Karena menggantikan semua materi pelajaran", "A"),
                            new QuestionSeed("Belajar efektif tidak selalu berarti apa?", "Belajar dalam waktu yang sangat lama", "Belajar dengan rencana", "Belajar secara bertahap", "Belajar dengan memahami materi", "A"),
                            new QuestionSeed("Apa manfaat membagi materi menjadi bagian kecil?", "Membantu memahami pelajaran lebih mendalam", "Membuat materi menjadi tidak penting", "Menghapus kebutuhan latihan", "Membuat siswa tidak perlu ujian", "A"),
                            new QuestionSeed("Kebiasaan apa yang dapat dihindari dengan belajar terencana?", "Belajar mendadak sebelum ujian", "Membaca catatan secara rutin", "Membuat jadwal belajar", "Mengulang materi secara bertahap", "A")
                    )
            );

            seedText(
                    ekonomi,
                    "Mengelola Uang Saku dengan Bijak",
                    "Mengelola uang saku merupakan keterampilan sederhana yang penting dipelajari sejak dini. Dengan pengelolaan yang baik, seseorang dapat membedakan antara kebutuhan dan keinginan. Kebutuhan adalah hal yang benar-benar diperlukan, seperti makanan atau transportasi, sedangkan keinginan adalah hal yang diinginkan tetapi tidak selalu mendesak.\n\n" +
                    "Salah satu cara mengelola uang saku adalah membuat catatan pengeluaran. Catatan ini membantu seseorang mengetahui ke mana uangnya digunakan dan bagian mana yang bisa dihemat. Selain itu, menyisihkan sebagian uang untuk ditabung dapat melatih kedisiplinan dan membantu seseorang mempersiapkan kebutuhan di masa depan.",
                    "admin-1",
                    List.of(
                            new QuestionSeed("Mengapa mengelola uang saku penting?", "Agar dapat membedakan kebutuhan dan keinginan", "Agar uang cepat habis", "Agar semua keinginan langsung dibeli", "Agar tidak perlu menabung", "A"),
                            new QuestionSeed("Apa contoh kebutuhan?", "Makanan atau transportasi", "Barang lucu yang sedang tren", "Mainan yang tidak diperlukan", "Aksesori tambahan", "A"),
                            new QuestionSeed("Apa manfaat membuat catatan pengeluaran?", "Mengetahui ke mana uang digunakan", "Membuat uang bertambah tanpa usaha", "Menghapus semua kebutuhan", "Membuat seseorang tidak perlu bekerja", "A"),
                            new QuestionSeed("Apa manfaat menabung?", "Mempersiapkan kebutuhan di masa depan", "Membuat uang langsung habis", "Menghindari semua perencanaan", "Membeli semua barang tanpa berpikir", "A"),
                            new QuestionSeed("Apa perbedaan kebutuhan dan keinginan?", "Kebutuhan diperlukan, keinginan tidak selalu mendesak", "Kebutuhan selalu mahal, keinginan selalu murah", "Kebutuhan tidak penting, keinginan wajib dibeli", "Keduanya selalu sama", "A")
                    )
            );

            seedText(
                    teknologi,
                    "Kecerdasan Buatan dalam Kehidupan Sehari-hari",
                    "Kecerdasan buatan atau artificial intelligence adalah teknologi yang memungkinkan komputer melakukan tugas yang biasanya membutuhkan kecerdasan manusia. Contohnya adalah mengenali suara, memberikan rekomendasi film, menerjemahkan bahasa, dan membantu pencarian informasi. Tanpa disadari, banyak orang sudah menggunakan kecerdasan buatan dalam aktivitas sehari-hari melalui aplikasi di ponsel mereka.\n\n" +
                    "Meskipun bermanfaat, penggunaan kecerdasan buatan juga perlu dilakukan secara bijak. Pengguna tetap harus memahami bahwa hasil dari sistem AI tidak selalu benar dan dapat dipengaruhi oleh data yang digunakan. Oleh karena itu, manusia tetap perlu berpikir kritis, memeriksa hasil yang diberikan, dan tidak menyerahkan seluruh keputusan penting kepada teknologi.",
                    "admin-1",
                    List.of(
                            new QuestionSeed("Apa yang dimaksud dengan kecerdasan buatan?", "Teknologi yang memungkinkan komputer melakukan tugas seperti kecerdasan manusia", "Teknologi untuk membuat komputer tidak bisa digunakan", "Aplikasi khusus untuk menggambar manual", "Perangkat yang hanya digunakan di pabrik", "A"),
                            new QuestionSeed("Apa contoh penggunaan AI sehari-hari?", "Rekomendasi film dan pengenalan suara", "Menanam tanaman tanpa air", "Menghapus semua data internet", "Mengganti listrik dengan kertas", "A"),
                            new QuestionSeed("Mengapa penggunaan AI perlu bijak?", "Karena hasil AI tidak selalu benar", "Karena AI selalu sempurna", "Karena AI tidak membutuhkan data", "Karena AI tidak pernah digunakan manusia", "A"),
                            new QuestionSeed("Apa yang harus dilakukan terhadap hasil AI?", "Memeriksa dan berpikir kritis", "Menerima semua hasil tanpa pertanyaan", "Menghapus semua hasil", "Mengabaikan semua teknologi", "A"),
                            new QuestionSeed("Apa yang tidak boleh dilakukan dalam penggunaan AI?", "Menyerahkan seluruh keputusan penting kepada teknologi", "Menggunakan AI sebagai alat bantu", "Mengecek kembali informasi", "Memahami keterbatasan teknologi", "A")
                    )
            );

            seedText(
                    budaya,
                    "Menjaga Bahasa Daerah sebagai Warisan Budaya",
                    "Bahasa daerah merupakan bagian penting dari identitas budaya suatu masyarakat. Melalui bahasa daerah, nilai, cerita rakyat, tradisi, dan cara pandang suatu kelompok dapat diwariskan dari generasi ke generasi. Namun, penggunaan bahasa daerah mulai berkurang di beberapa lingkungan karena masyarakat lebih sering menggunakan bahasa nasional atau bahasa asing.\n\n" +
                    "Menjaga bahasa daerah bukan berarti menolak bahasa lain. Sebaliknya, seseorang tetap dapat menguasai bahasa nasional dan bahasa asing sambil mempertahankan bahasa daerahnya. Upaya pelestarian dapat dilakukan melalui penggunaan bahasa daerah di keluarga, kegiatan budaya, pembelajaran di sekolah, dan dokumentasi cerita lokal.",
                    "admin-1",
                    List.of(
                            new QuestionSeed("Mengapa bahasa daerah penting?", "Karena menjadi bagian dari identitas budaya", "Karena menggantikan semua bahasa lain", "Karena hanya digunakan untuk hiburan", "Karena tidak memiliki nilai sejarah", "A"),
                            new QuestionSeed("Apa yang dapat diwariskan melalui bahasa daerah?", "Nilai, cerita rakyat, dan tradisi", "Hanya angka matematika", "Kode komputer", "Peraturan lalu lintas", "A"),
                            new QuestionSeed("Mengapa penggunaan bahasa daerah mulai berkurang?", "Karena masyarakat lebih sering menggunakan bahasa nasional atau asing", "Karena bahasa daerah dilarang di semua tempat", "Karena bahasa daerah tidak pernah digunakan", "Karena semua bahasa daerah sama", "A"),
                            new QuestionSeed("Menjaga bahasa daerah berarti apa?", "Mempertahankan bahasa daerah tanpa menolak bahasa lain", "Menolak semua bahasa nasional", "Tidak boleh belajar bahasa asing", "Menghapus bahasa dari sekolah", "A"),
                            new QuestionSeed("Apa contoh upaya pelestarian bahasa daerah?", "Menggunakannya di keluarga dan kegiatan budaya", "Melarang orang berbicara", "Menghapus cerita lokal", "Tidak mendokumentasikan tradisi", "A")
                    )
            );

            seedText(
                    kewarganegaraan,
                    "Tanggung Jawab Warga Negara di Lingkungan Sekitar",
                    "Warga negara memiliki hak dan kewajiban dalam kehidupan bermasyarakat. Hak dapat berupa kesempatan memperoleh pendidikan, rasa aman, dan pelayanan publik. Sementara itu, kewajiban mencakup menaati aturan, menjaga ketertiban, dan menghormati hak orang lain. Keseimbangan antara hak dan kewajiban diperlukan agar kehidupan bersama berjalan harmonis.\n\n" +
                    "Tanggung jawab warga negara tidak hanya dilakukan dalam skala besar, tetapi juga dimulai dari lingkungan terdekat. Contohnya adalah membuang sampah pada tempatnya, ikut menjaga fasilitas umum, menghormati tetangga, dan berpartisipasi dalam kegiatan masyarakat. Dengan menjalankan tanggung jawab tersebut, seseorang ikut membangun lingkungan yang tertib, aman, dan nyaman.",
                    "admin-1",
                    List.of(
                            new QuestionSeed("Apa yang dimiliki warga negara dalam masyarakat?", "Hak dan kewajiban", "Hanya hak tanpa kewajiban", "Hanya kewajiban tanpa hak", "Tidak memiliki peran apa pun", "A"),
                            new QuestionSeed("Apa contoh hak warga negara?", "Kesempatan memperoleh pendidikan", "Melanggar aturan", "Merusak fasilitas umum", "Mengabaikan hak orang lain", "A"),
                            new QuestionSeed("Apa contoh kewajiban warga negara?", "Menaati aturan dan menjaga ketertiban", "Mengambil hak orang lain", "Membuang sampah sembarangan", "Menolak semua kegiatan masyarakat", "A"),
                            new QuestionSeed("Di mana tanggung jawab warga negara dapat dimulai?", "Dari lingkungan terdekat", "Hanya di gedung pemerintahan", "Hanya saat pemilu", "Hanya di media sosial", "A"),
                            new QuestionSeed("Apa dampak menjalankan tanggung jawab di lingkungan?", "Menciptakan lingkungan tertib, aman, dan nyaman", "Membuat lingkungan menjadi kacau", "Mengurangi rasa aman", "Merusak hubungan sosial", "A")
                    )
            );
        }
    }

    private void seedText(
            Category cat,
            String title,
            String content,
            String creatorId,
            List<QuestionSeed> questions
    ) {
        Text text = new Text(title, content, cat, creatorId);
        textRepository.save(text);

        for (QuestionSeed seed : questions) {
            Question q = new Question();
            q.setText(text);
            q.setQuestion(seed.questionText());
            questionRepository.save(q);

            optionRepository.saveAll(List.of(
                    createOption(q, seed.optionA(), "A".equals(seed.correctAnswer())),
                    createOption(q, seed.optionB(), "B".equals(seed.correctAnswer())),
                    createOption(q, seed.optionC(), "C".equals(seed.correctAnswer())),
                    createOption(q, seed.optionD(), "D".equals(seed.correctAnswer()))
            ));
        }
    }

    private Option createOption(Question q, String text, boolean correct) {
        Option opt = new Option(text, correct);
        opt.setQuestion(q);
        return opt;
    }

    private record QuestionSeed(
            String questionText,
            String optionA,
            String optionB,
            String optionC,
            String optionD,
            String correctAnswer
    ) {
    }
}