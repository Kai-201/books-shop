import { defineStore } from "pinia";
import { ref } from "vue";

/** 跨页面同步图书库存（下单、管理图书后触发刷新） */
export const useInventoryStore = defineStore("inventory", () => {
  const revision = ref(0);
  const stocks = ref({});

  function setBooks(books) {
    const next = { ...stocks.value };
    books.forEach((book) => {
      next[book.id] = book.booksNum;
    });
    stocks.value = next;
  }

  function setBookStock(bookId, booksNum) {
    stocks.value = { ...stocks.value, [bookId]: booksNum };
  }

  function getStock(bookId, fallback) {
    const cached = stocks.value[bookId];
    return cached !== undefined ? cached : fallback;
  }

  function bump() {
    revision.value += 1;
  }

  return { revision, stocks, setBooks, setBookStock, getStock, bump };
});
