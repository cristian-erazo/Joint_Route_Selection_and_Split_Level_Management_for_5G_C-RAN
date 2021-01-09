graph [
  node [
    id 0
    label "0"
    type 2
    prc 3
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
    ant 5
    prb 5
    x 70
    y 79
  ]
  node [
    id 3
    label "3"
    type 1
    prc 1
    ant 7
    prb 4
    x 50
    y 81
  ]
  node [
    id 4
    label "4"
    type 1
    prc 1
    ant 2
    prb 2
    x 27
    y 38
  ]
  node [
    id 5
    label "5"
    type 1
    prc 2
    ant 3
    prb 2
    x 38
    y 103
  ]
  node [
    id 6
    label "6"
    type 1
    prc 3
    ant 7
    prb 4
    x 73
    y 61
  ]
  edge [
    source 0
    target 6
    bandwith 142
    delay 343
  ]
  edge [
    source 1
    target 3
    bandwith 663
    delay 176
  ]
  edge [
    source 1
    target 2
    bandwith 818
    delay 101
  ]
  edge [
    source 1
    target 5
    bandwith 242
    delay 399
  ]
  edge [
    source 1
    target 4
    bandwith 628
    delay 171
  ]
]
