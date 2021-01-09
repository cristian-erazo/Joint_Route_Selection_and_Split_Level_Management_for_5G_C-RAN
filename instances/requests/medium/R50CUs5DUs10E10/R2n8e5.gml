graph [
  node [
    id 0
    label "0"
    type 2
    prc 5
  ]
  node [
    id 1
    label "1"
    type 2
    prc 5
  ]
  node [
    id 2
    label "2"
    type 2
    prc 2
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 6
    prb 5
    x 12
    y 75
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 10
    prb 2
    x 73
    y 99
  ]
  node [
    id 5
    label "5"
    type 1
    prc 1
    ant 7
    prb 4
    x 76
    y 61
  ]
  node [
    id 6
    label "6"
    type 1
    prc 3
    ant 7
    prb 2
    x 11
    y 52
  ]
  node [
    id 7
    label "7"
    type 1
    prc 5
    ant 8
    prb 3
    x 39
    y 92
  ]
  edge [
    source 0
    target 6
    bandwith 700
    delay 343
  ]
  edge [
    source 1
    target 3
    bandwith 907
    delay 321
  ]
  edge [
    source 1
    target 7
    bandwith 612
    delay 266
  ]
  edge [
    source 2
    target 5
    bandwith 760
    delay 159
  ]
  edge [
    source 2
    target 4
    bandwith 681
    delay 163
  ]
]
