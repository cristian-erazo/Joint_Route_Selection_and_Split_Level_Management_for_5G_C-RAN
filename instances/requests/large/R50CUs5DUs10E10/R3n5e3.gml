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
    prc 4
  ]
  node [
    id 2
    label "2"
    type 1
    prc 4
    ant 2
    prb 3
    x 92
    y 104
  ]
  node [
    id 3
    label "3"
    type 1
    prc 1
    ant 2
    prb 5
    x 51
    y 114
  ]
  node [
    id 4
    label "4"
    type 1
    prc 1
    ant 7
    prb 5
    x 116
    y 87
  ]
  edge [
    source 0
    target 4
    bandwith 952
    delay 217
  ]
  edge [
    source 1
    target 2
    bandwith 861
    delay 426
  ]
  edge [
    source 1
    target 3
    bandwith 953
    delay 154
  ]
]
