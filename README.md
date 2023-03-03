# LogUtils

### 將收到的Json Log完整印出，並自動加上呼叫地方(仿照Timber)的Tag的工具。</p>

### 原理：Android Studio 的 Logcat 有最大字數，每行5000字的設定。若超過會自動折行，但是部分資料會缺失。因此，減少其每行5000字的設定(經實測，3000字最為保險)，即可完整印出。</p>

步驟 1.將 JitPack 存儲庫添加到您的構建文件</p>

<pre><code>
allprojects {
    repositories {
    
      //... 
      maven { url 'https://jitpack.io' }
      //...
    }
}
</code></pre>
</p>
步驟 2.添加依賴</p>
<pre><code>
dependencies {
    implementation 'com.github.HsiangxMinxHsieh:LogUtils:v1.0.0'
}
 </code></pre>
</p>
※修改每行字數：</p>
<pre><code>
LogOption.LOG_MAX_LENGTH = 500 // 將每行字數修改為500

</code></pre>
