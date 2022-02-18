(ns donut.todo-example.backend.endpoint.todo
  (:require [honey.sql :as sql]
            [next.jdbc.sql :as jsql]))

(def handlers
  {;; "/todo"
   :collection
   {:get (fn [{:keys [db]}]
           {:status 200
            :body   (->> {:select [:*]
                          :from   [:todo]}
                         sql/format
                         (jsql/query db)
                         (into []))})}

   ;; "/todo/:id"
   :member
   {:get {:parameters {:path [:map [:id int?]]}
          :handler    (fn [{:keys [all-params db]}]
                        {:status 200
                         :body   (->> {:select [:*]
                                       :from   [:todo]
                                       :where  [:= :id (:id all-params)]}
                                      sql/format
                                      (jsql/query db)
                                      (into []))})}

    :put {:parameters {:path [:map [:id int?]]}
          :handler    (fn [{:keys [all-params db]}]
                        (jsql/update! db
                                      :todo
                                      (dissoc all-params :id)
                                      (select-keys all-params [:id]))
                        {:status 200
                         :body   (->> {:select [:*]
                                       :from   [:todo]
                                       :where  [:= :id (:id all-params)]}
                                      sql/format
                                      (jsql/query db)
                                      (into []))})}

    :delete {:parameters {:path [:map [:id int?]]}
             :handler    (fn [{:keys [all-params db]}]
                           (jsql/delete! db :todo (select-keys all-params [:id])))}}})
