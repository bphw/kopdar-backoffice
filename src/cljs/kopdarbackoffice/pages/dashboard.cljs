(ns kopdarbackoffice.pages.dashboard
  (:require ["@inertiajs/inertia-react" :refer [Head]]))

(defn index []
  [:<>
   [:> Head {:title "Dashboard"}]
   [:div [:h1.mb-8.font-bold.text-3xl "Dashboard"]
    [:p.mb-8.leading-normal "Welcome to " [:b "Kopdar Backoffice"] ", a demo app designed to manage Master Table from Kopdar"]
    [:p.mb-8.leading-normal "More information about the Inertia Clojure adapter "
     [:a.text-indigo-500.underline.hover:text-orange-600
      {:href "https://github.com/prestancedesign/inertia-clojure"} "here."]]
    [:div.p-6.max-w-sm.mx-auto.bg-white.rounded-xl.shadow-lg.flex.items-center.space-x-4
     [:div.shrink-0]
     [:div
      [:div.text-xl.font-medium.text-black "Kategori 1"]
      [:p.text-slate-500 [:a {:href "/categories-one"} "Pengaturan master kategori pertama"]]]]
    [:div.p-6.max-w-sm.mx-auto.bg-white.rounded-xl.shadow-lg.flex.items-center.space-x-4
     [:div.shrink-0]
     [:div
      [:div.text-xl.font-medium.text-black "Kategori 2"]
      [:p.text-slate-500 [:a {:href "/categories-two"} "Pengaturan master kategori kedua"]]]]
    [:div.p-6.max-w-sm.mx-auto.bg-white.rounded-xl.shadow-lg.flex.items-center.space-x-4
     [:div.shrink-0]
     [:div
      [:div.text-xl.font-medium.text-black "Kategori 3"]
      [:p.text-slate-500 [:a {:href "/categories-three"} "Pengaturan master kategori ketiga"]]]]
    [:div.p-6.max-w-sm.mx-auto.bg-white.rounded-xl.shadow-lg.flex.items-center.space-x-4
     [:div.shrink-0]
     [:div
      [:div.text-xl.font-medium.text-black "Etalase"]
      [:p.text-slate-500 [:a {:href "/etalases"} "Pengaturan etalase"]]]]
    [:div.p-6.max-w-sm.mx-auto.bg-white.rounded-xl.shadow-lg.flex.items-center.space-x-4
     [:div.shrink-0]
     [:div
      [:div.text-xl.font-medium.text-black "Metode Bayar"]
      [:p.text-slate-500 [:a {:href "/payment-methods"} "Pengaturan Metode Pembayaran"]]]]
    [:div.p-6.max-w-sm.mx-auto.bg-white.rounded-xl.shadow-lg.flex.items-center.space-x-4
     [:div.shrink-0]
     [:div
      [:div.text-xl.font-medium.text-black "Pengiriman"]
      [:p.text-slate-500 [:a {:href "/deliveries"} "Pengaturan Pengiriman"]]]]
    [:div.p-6.max-w-sm.mx-auto.bg-white.rounded-xl.shadow-lg.flex.items-center.space-x-4
     [:div.shrink-0]
     [:div
      [:div.text-xl.font-medium.text-black "Status Transaksi"]
      [:p.text-slate-500 [:a {:href "/trx-statuses"} "Jenis Status Transaksi"]]]]
    [:div.p-6.max-w-sm.mx-auto.bg-white.rounded-xl.shadow-lg.flex.items-center.space-x-4
     [:div.shrink-0]
     [:div
      [:div.text-xl.font-medium.text-black "Group Pengguna"]
      [:p.text-slate-500 [:a {:href "/user-groups"} "Jenis Group Pengguna"]]]]
    [:div.p-6.max-w-sm.mx-auto.bg-white.rounded-xl.shadow-lg.flex.items-center.space-x-4
     [:div.shrink-0]
     [:div
      [:div.text-xl.font-medium.text-black "Rating Produk"]
      [:p.text-slate-500 [:a {:href "/rate-products"} "Pengaturan Rating Produk"]]]]
    [:div.p-6.max-w-sm.mx-auto.bg-white.rounded-xl.shadow-lg.flex.items-center.space-x-4
     [:div.shrink-0]
     [:div
      [:div.text-xl.font-medium.text-black "Rating Toko"]
      [:p.text-slate-500 [:a {:href "/rate-stores"} "Pengaturan Rating Toko"]]]]
    [:div.p-6.max-w-sm.mx-auto.bg-white.rounded-xl.shadow-lg.flex.items-center.space-x-4
     [:div.shrink-0]
     [:div
      [:div.text-xl.font-medium.text-black "Chat Transaksi"]
      [:p.text-slate-500 [:a {:href "/chats"} "Chat saat transaksi"]]]]
    [:div.p-6.max-w-sm.mx-auto.bg-white.rounded-xl.shadow-lg.flex.items-center.space-x-4
     [:div.shrink-0]
     [:div
      [:div.text-xl.font-medium.text-black "Transaksi penjualan detail"]
      [:p.text-slate-500 [:a {:href "/trx-details"} "Informasi detail transaksi"]]]]
    [:div.p-6.max-w-sm.mx-auto.bg-white.rounded-xl.shadow-lg.flex.items-center.space-x-4
     [:div.shrink-0]
     [:div
      [:div.text-xl.font-medium.text-black "Transaksi penjualan header"]
      [:p.text-slate-500 [:a {:href "/trx-headers"} "Informasi header transaksi"]]]]
    ]])
