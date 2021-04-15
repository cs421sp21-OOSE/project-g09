import React, { Fragment, useState } from "react";
import { Listbox, Transition } from "@headlessui/react";
import { CheckIcon } from "@heroicons/react/solid";

const Select = (props) => {
  const [selected, setSelected] = useState(props.options[0]);
  if (selected) {
    return (
      <div className="z-50 w-full">
        <Listbox
          value={selected}
          onChange={(value) => {
            setSelected(value);
            props.setOptionSelected(value.name);
          }}
        >
          {({ open }) => (
            <>
              <div className="z-50 relative w-full">
                <Listbox.Button className="flex w-full text-left bg-white rounded-lg focus:outline-none text-xl md:text-2xl">
                  <div className="w-full flex justify-between content-center">
                    <span className="block">{selected.name}</span>
                    <span className="flex content-center z-50">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                        className="w-4 sm:w-5 justify-right content-center"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M19 9l-7 7-7-7"
                        />
                      </svg>
                    </span>
                  </div>
                </Listbox.Button>
                <Transition
                  show={open}
                  as={Fragment}
                  leave="transition ease-in duration-100"
                  leaveFrom="opacity-100"
                  leaveTo="opacity-0"
                >
                  <Listbox.Options
                    static
                    className="absolute right-0 w-full py-1 mt-1 overflow-auto text-gray-700 bg-white rounded-md shadow-lg max-h-60 focus:outline-none sm:text-sm"
                  >
                    {props.options.map((option, optionIdx) => {
                      if (option.name !== selected.name) {
                        return (
                          <Listbox.Option
                            key={optionIdx}
                            className={({ active }) =>
                              `${
                                active
                                  ? "hover:bg-gray-100"
                                  : "text-cool-gray-900"
                              }
                    cursor-default select-none relative py-1 px-2 text-2xl`
                            }
                            value={option}
                          >
                            {({ selected, active }) => (
                              <>
                                <span
                                  className={`${
                                    selected ? "font-medium" : "font-normal"
                                  } block truncate`}
                                >
                                  {option.name}
                                </span>
                              </>
                            )}
                          </Listbox.Option>
                        );
                      }
                    })}
                  </Listbox.Options>
                </Transition>
              </div>
            </>
          )}
        </Listbox>
      </div>
    );
  } else return "";
};

export default Select;
